package ru.aakumykov.me.sociocat.utils;

import androidx.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ru.aakumykov.me.sociocat.interfaces.iAuthSingleton;
import ru.aakumykov.me.sociocat.interfaces.iAuthStateListener;
import ru.aakumykov.me.sociocat.interfaces.iUsersSingleton;
import ru.aakumykov.me.sociocat.models.User;
import ru.aakumykov.me.sociocat.singletons.AuthSingleton;
import ru.aakumykov.me.sociocat.singletons.UsersSingleton;

// TODO: как обрабатывать reauthenticate ?

public class AuthStateListener implements iAuthStateListener {

    private final static String TAG = "AuthStateListener";
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    public AuthStateListener(final iAuthStateListener.StateChangeCallbacks callbacks) {
        Log.d(TAG, "new AuthStateListener()");

        final iAuthSingleton authSingleton = AuthSingleton.getInstance();
        final iUsersSingleton usersSingleton = UsersSingleton.getInstance();

        // TODO: сильно часто вызывается. Разобраться.
        firebaseAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth fba) {

                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                if (null != firebaseUser) {

                    usersSingleton.getUserById(firebaseUser.getUid(), new iUsersSingleton.ReadCallbacks() {
                        @Override
                        public void onUserReadSuccess(User user) {
                            authSingleton.storeCurrentUser(user);
                            callbacks.onLoggedIn();
                        }

                        @Override
                        public void onUserReadFail(String errorMsg) {
                            authSingleton.clearCurrentUser();
                        }
                    });

                } else {
                    callbacks.onLoggedOut();
                    authSingleton.clearCurrentUser();
                }
            }
        });
    }

}
