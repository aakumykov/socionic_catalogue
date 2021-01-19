package io.gitlab.aakumykov.sociocat;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.greenrobot.eventbus.EventBus;

import io.gitlab.aakumykov.sociocat.constants.PreferencesConstants;
import io.gitlab.aakumykov.sociocat.event_bus_objects.UserAuthorizedEvent;
import io.gitlab.aakumykov.sociocat.event_bus_objects.UserUnauthorizedEvent;
import io.gitlab.aakumykov.sociocat.models.User;
import io.gitlab.aakumykov.sociocat.singletons.UsersSingleton;
import io.gitlab.aakumykov.sociocat.singletons.iUsersSingleton;

public class SocioCatApp extends Application {

    private final static String TAG = SocioCatApp.class.getSimpleName();
    private iUsersSingleton usersSingleton;

    @Override
    public void onCreate() {
        super.onCreate();
        usersSingleton = UsersSingleton.getInstance();
        subscribeToAuthorizationEvents();
    }

    private void subscribeToAuthorizationEvents() {

        // Подписываюсь на события изменения авторизации Firebase
        FirebaseAuth.getInstance().addAuthStateListener(firebaseAuth -> {

            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

            if (null != firebaseUser) {

                String userId = firebaseUser.getUid();

                try {
                    usersSingleton.refreshUserFromServer(userId, new iUsersSingleton.iRefreshCallbacks() {
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
        rememberCurrentUserId(user);
    }

    private void deauthorizeUser() {
        forgetCurrentUserId();
        usersSingleton.clearCurrentUser();
        EventBus.getDefault().post(new UserUnauthorizedEvent());
    }

    private void rememberCurrentUserId(@NonNull User user) {
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putString(PreferencesConstants.key_current_user_id, user.getKey())
                .apply();
    }

    private void forgetCurrentUserId() {
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .remove(PreferencesConstants.key_current_user_id)
                .apply();
    }

}
