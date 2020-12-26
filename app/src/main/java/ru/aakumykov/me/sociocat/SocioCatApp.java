package ru.aakumykov.me.sociocat;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.greenrobot.eventbus.EventBus;

import ru.aakumykov.me.sociocat.event_bus_objects.UserAuthorizedEvent;
import ru.aakumykov.me.sociocat.event_bus_objects.UserUnauthorizedEvent;
import ru.aakumykov.me.sociocat.models.User;
import ru.aakumykov.me.sociocat.singletons.UsersSingleton;
import ru.aakumykov.me.sociocat.singletons.iUsersSingleton;

public class SocioCatApp extends Application {

    private final static String TAG = SocioCatApp.class.getSimpleName();
    private final iUsersSingleton usersSingleton = UsersSingleton.getInstance();

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        subscribeToAuthorizationEvents();
    }

    private void subscribeToAuthorizationEvents() {

        // Подписываюсь на события изменения авторизации Firebase
        FirebaseAuth.getInstance().addAuthStateListener(firebaseAuth -> {

            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

            if (null != firebaseUser) {

                String userId = firebaseUser.getUid();

                try {
                    usersSingleton.refreshUserFromServer(userId, new iUsersSingleton.RefreshCallbacks() {
                        @Override
                        public void onUserRefreshSuccess(@NonNull User user) {
                            authorizeUser(user);
                        }

                        @Override
                        public void onUserNotExists() {
                            Log.e(TAG, "User with id "+userId+" does not exists on server.");
                            deauthorizeUser();
                        }

                        @Override
                        public void onUserRefreshFail(String errorMsg) {
                            Log.e(TAG, "Error refreshing user from server: "+errorMsg);
                            deauthorizeUser();
                        }
                    });
                }
                catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                    deauthorizeUser();
                }

            }
            else {
                deauthorizeUser();
            }
        });
    }

    private void authorizeUser(User user) {
        EventBus.getDefault().post(new UserAuthorizedEvent(user));
    }

    private void deauthorizeUser() {
        EventBus.getDefault().post(new UserUnauthorizedEvent());
        usersSingleton.clearCurrentUser();
    }

}
