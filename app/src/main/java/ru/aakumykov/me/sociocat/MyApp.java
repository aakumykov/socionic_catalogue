package ru.aakumykov.me.sociocat;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import ru.aakumykov.me.sociocat.backup_job.BackupService;
import ru.aakumykov.me.sociocat.event_bus_objects.NewCardEvent;
import ru.aakumykov.me.sociocat.event_bus_objects.UserAuthorizedEvent;
import ru.aakumykov.me.sociocat.event_bus_objects.UserUnauthorizedEvent;
import ru.aakumykov.me.sociocat.models.User;
import ru.aakumykov.me.sociocat.preferences.PreferencesProcessor;
import ru.aakumykov.me.sociocat.singletons.UsersSingleton;
import ru.aakumykov.me.sociocat.singletons.iUsersSingleton;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class MyApp extends Application implements iMyApp {

    private final static String TAG = "=MyApp=";
    private iUsersSingleton usersSingleton;
    private volatile int newCardsCount = 0;

    // Методы Application
    @Override
    public void onCreate() {
        super.onCreate();

        usersSingleton = UsersSingleton.getInstance();

        // Подписываюсь на события изменения авторизации Firebase
        FirebaseAuth.getInstance().addAuthStateListener(firebaseAuth -> {

            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

            if (null != firebaseUser) {

                String userId = firebaseUser.getUid();

                try {
                    usersSingleton.refreshUserFromServer(userId, new iUsersSingleton.RefreshCallbacks() {
                        @Override
                        public void onUserRefreshSuccess(User user) {
                            if (null != user)
                                authorizeUser(user);
                        }

                        @Override
                        public void onUserNotExists() {

                        }

                        @Override
                        public void onUserRefreshFail(String errorMsg) {
                            deauthorizeUser();
                        }
                    });
                }
                catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                    e.printStackTrace();
                    deauthorizeUser();
                }

            } else {
                Log.e(TAG, "FirebaseUser == NULL");
                deauthorizeUser();
            }
        });

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        prepareDefaultPreferences();

        produceBackup();

        logFCMRegistrationToken();

        subscribe2topic("new_cards");
        subscribe2topic("updated_cards");

    }

    // iMyApp
    @Override
    public boolean isNewCardsAvailable() {
        return newCardsCount > 0;
    }

    @Override
    public int getNewCardsCount() {
        return newCardsCount;
    }

    @Override
    public void resetNewCardsCounter() {
        setNewCardsCount(0);
    }

    private synchronized void setNewCardsCount(int value) {

    }

    // Подписка на новые карточки
    @Subscribe
    void onNewCardArrived(NewCardEvent newCardEvent) {
        setNewCardsCount(newCardsCount+1);
    }


    // Внутренние методы
    private void authorizeUser(User user) {
        Log.d(TAG, "authorizeUser(), "+user.getName());
        EventBus.getDefault().post(new UserAuthorizedEvent(user));
    }

    private void deauthorizeUser() {
        Log.d(TAG, "deauthorizeUser()");
        EventBus.getDefault().post(new UserUnauthorizedEvent());
        usersSingleton.clearCurrentUser();
    }

    private void prepareDefaultPreferences() {

        // Подготавливаю необходимые компоненты
//        Context appContext = getApplicationContext();
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Проверяю, первый ли это запуск программы?
        boolean isFirstRun = defaultSharedPreferences.getBoolean(Constants.PREFERENCE_KEY_IS_FIRST_RUN, true);

        // Если это первый запуск, устанавдиваю в механизме настроек значения по умолчанию и обрабатываю их все
        if (isFirstRun) {
            PreferenceManager.setDefaultValues(this, R.xml.preferences, true);
            PreferencesProcessor.processAllPreferences(this, defaultSharedPreferences);

            // Помечаю, что теперь это не первый запуск
            SharedPreferences.Editor editor = defaultSharedPreferences.edit();
            editor.putBoolean(Constants.PREFERENCE_KEY_IS_FIRST_RUN, false);
            editor.apply();
        }
    }

//    private void registerPushToken(User user) {
//        Log.d(TAG, "registerPushToken()");
//
//        FirebaseInstanceId.getInstance().getInstanceId()
//                .addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
//                    @Override public void onSuccess(InstanceIdResult instanceIdResult) {
//                        String devId = instanceIdResult.getId();
//
//                        usersSingleton.storeDeviceId(user.getKey(), devId, new iUsersSingleton.SaveDeviceIdCallbacks() {
//                            @Override public void onStoreDeviceIdSuccess() {
//                                Log.d(TAG, "device id saved: "+devId);
//                            }
//
//                            @Override public void onStoreDeviceIdFailed(String errorMSg) {
//                                Log.e(TAG, errorMSg);
//                            }
//                        });
//
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override public void onFailure(@NonNull Exception e) {
//
//                    }
//                });
//    }

//    private void checkPushToken(User user) {
//
//        if (null == user.getPushToken()) {
//
//            FirebaseInstanceId.getInstance().getInstanceId()
//                    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
//
//                        @Override
//                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
//
//                            if (!task.isSuccessful()) {
//                                Exception exception = task.getException();
//                                if (null != exception) {
//                                    Log.e(TAG, exception.getMessage());
//                                    exception.printStackTrace();
//                                }
//
//                            } else {
//
//                                InstanceIdResult instanceIdResult = task.getResult();
//
//                                if (null != instanceIdResult) {
//
//                                    String token = instanceIdResult.getToken();
//
//                                    usersSingleton.updatePushToken(token, new iUsersSingleton.PushTokenCallbacks() {
//                                        @Override
//                                        public void onPushTokenUpdateSuccess(String token) {
//                                            User user = usersSingleton.getCurrentUser();
//                                            user.setPushToken(token);
//                                            usersSingleton.storeCurrentUser(user); // TODO: добавляет неоднозначности
//                                        }
//
//                                        @Override
//                                        public void onPushTokenUpdateError(String errorMsg) {
//                                            Log.e(TAG, errorMsg);
//                                        }
//                                    });
//
//                                } else {
//                                    Log.e(TAG, "InstanceIdResult is NULL");
//                                }
//                            }
//                        }
//                    });
//
//        }
//    }

    private void produceBackup() {
        if (BackupService.isTimeToDoBackup(this)) {
            try {
                startService(new Intent(this, BackupService.class));
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    private void logFCMRegistrationToken() {

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        Log.d(TAG, "InstanceId: "+instanceIdResult.getId());
                        Log.d(TAG, "Token: "+instanceIdResult.getToken());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, e.getMessage());
                        Log.w(TAG, e);
                    }
                });
    }

    private void subscribe2topic(String topicName) {

        FirebaseMessaging.getInstance().subscribeToTopic(topicName)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Вы подписаны на уведомления '"+topicName+"'");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        MyUtils.printError(TAG, e);
                    }
                });
    }

    private void unsubscribeFromTopic(String topicName) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topicName)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Вы отписаны от уведомлений '"+topicName+"'");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        MyUtils.printError(TAG, e);
                    }
                });
    }
}
