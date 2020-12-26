package ru.aakumykov.me.sociocat;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.greenrobot.eventbus.EventBus;

import ru.aakumykov.me.sociocat.event_bus_objects.UserAuthorizedEvent;
import ru.aakumykov.me.sociocat.event_bus_objects.UserUnauthorizedEvent;
import ru.aakumykov.me.sociocat.models.User;
import ru.aakumykov.me.sociocat.singletons.UsersSingleton;
import ru.aakumykov.me.sociocat.singletons.iUsersSingleton;

public class SocioCatApp extends Application {

    private final static String TAG = "=SocioCatApp=";
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

        //logFCMRegistrationToken();
    }

    // Внутренние методы
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


    private void subscribeToAuthorizationEvents() {

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
    }

    private void authorizeUser(User user) {
        Log.d(TAG, "authorizeUser(), "+user.getName());
        EventBus.getDefault().post(new UserAuthorizedEvent(user));
    }

    private void deauthorizeUser() {
        Log.d(TAG, "deauthorizeUser()");
        EventBus.getDefault().post(new UserUnauthorizedEvent());
        usersSingleton.clearCurrentUser();
    }

}
