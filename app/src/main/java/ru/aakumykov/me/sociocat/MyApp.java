package ru.aakumykov.me.sociocat;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import ru.aakumykov.me.sociocat.event_bus_objects.NewCardEvent;
import ru.aakumykov.me.sociocat.event_bus_objects.NewCommentEvent;
import ru.aakumykov.me.sociocat.event_bus_objects.UserAuthorizedEvent;
import ru.aakumykov.me.sociocat.event_bus_objects.UserUnauthorizedEvent;
import ru.aakumykov.me.sociocat.push_notifications.NewCardNotification_Helper;
import ru.aakumykov.me.sociocat.push_notifications.NewCommentNotification_Helper;
import ru.aakumykov.me.sociocat.models.User;
import ru.aakumykov.me.sociocat.singletons.UsersSingleton;
import ru.aakumykov.me.sociocat.singletons.iUsersSingleton;

public class MyApp extends Application {

    private final static String TAG = "=MyApp=";
    private iUsersSingleton usersSingleton;


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

        prepareDefaultPreferences();

        logFCMRegistrationToken();

        EventBus.getDefault().register(this);
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
        boolean isFirstRun = defaultSharedPreferences.getBoolean(Constants.PREFERENCE_KEY_is_first_run, true);

        // Если это первый запуск, устанавдиваю в механизме настроек значения по умолчанию и обрабатываю их все
        if (isFirstRun) {
            PreferenceManager.setDefaultValues(this, R.xml.preferences, true);
            //PreferencesProcessor.processAllPreferences(this, defaultSharedPreferences);

            // Помечаю, что теперь это не первый запуск
            SharedPreferences.Editor editor = defaultSharedPreferences.edit();
            editor.putBoolean(Constants.PREFERENCE_KEY_is_first_run, false);
            editor.apply();
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

}
