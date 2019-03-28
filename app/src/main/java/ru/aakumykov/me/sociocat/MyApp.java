package ru.aakumykov.me.sociocat;

import android.app.Application;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.greenrobot.eventbus.EventBus;

import androidx.annotation.NonNull;
import ru.aakumykov.me.sociocat.event_objects.UserAuthorizedEvent;
import ru.aakumykov.me.sociocat.event_objects.UserUnauthorizedEvent;
import ru.aakumykov.me.sociocat.interfaces.iAuthSingleton;
import ru.aakumykov.me.sociocat.interfaces.iUsersSingleton;
import ru.aakumykov.me.sociocat.models.User;
import ru.aakumykov.me.sociocat.services.AuthSingleton;
import ru.aakumykov.me.sociocat.services.UsersSingleton;

public class MyApp extends Application {

    private final static String TAG = "MyApp";
    private iAuthSingleton authService;
    private iUsersSingleton usersService;


    @Override public void onCreate() {
        super.onCreate();

        authService = AuthSingleton.getInstance();
        usersService = UsersSingleton.getInstance();

        FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {

            @Override public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                if (null != firebaseUser) {
                    String uid = firebaseUser.getUid();

                    usersService.getUserById(uid, new iUsersSingleton.ReadCallbacks() {
                        @Override
                        public void onUserReadSuccess(User user) {
                            EventBus.getDefault().post(new UserAuthorizedEvent(user));
                        }

                        @Override
                        public void onUserReadFail(String errorMsg) {
                            // TODO: что делать с ошибкой?
                            Log.e(TAG, errorMsg);
                        }
                    });

                } else {
                    EventBus.getDefault().post(new UserUnauthorizedEvent());
                }
            }
        });
    }


    private void checkPushToken(User user) {

        if (null == user.getPushToken()) {

            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {

                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {

                            if (!task.isSuccessful()) {
                                Exception exception = task.getException();
                                if (null != exception) {
                                    Log.e(TAG, exception.getMessage());
                                    exception.printStackTrace();
                                }

                            } else {

                                InstanceIdResult instanceIdResult = task.getResult();

                                if (null != instanceIdResult) {

                                    String token = instanceIdResult.getToken();

                                    usersService.updatePushToken(token, new iUsersSingleton.PushTokenCallbacks() {
                                        @Override
                                        public void onPushTokenUpdateSuccess(String token) {
                                            User user = authService.currentUser();
                                            user.setPushToken(token);
                                            authService.storeCurrentUser(user); // TODO: добавляет неоднозначности
                                        }

                                        @Override
                                        public void onPushTokenUpdateError(String errorMsg) {
                                            Log.e(TAG, errorMsg);
                                        }
                                    });

                                } else {
                                    Log.e(TAG, "InstanceIdResult is NULL");
                                }
                            }
                        }
                    });

        }
    }
}
