package ru.aakumykov.me.sociocat.services;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ru.aakumykov.me.sociocat.interfaces.iAuthSingleton;
import ru.aakumykov.me.sociocat.interfaces.iAuthStateListener;
import ru.aakumykov.me.sociocat.interfaces.iUsersSingleton;
import ru.aakumykov.me.sociocat.models.User;

// TODO: как обрабатывать reauthenticate ?

public class AuthStateListener implements iAuthStateListener {

    private final static String TAG = "AuthStateListener";
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    public AuthStateListener(final iAuthStateListener.StateChangeCallbacks callbacks) {
        Log.d(TAG, "new AuthStateListener()");

        final iAuthSingleton authService = AuthSingleton.getInstance();
        final iUsersSingleton usersService = UsersSingleton.getInstance();

        // TODO: сильно часто вызывается. Разобраться.
        firebaseAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth fba) {

                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                if (null != firebaseUser) {

                    callbacks.onLoggedIn();

                    usersService.getUserById(firebaseUser.getUid(), new iUsersSingleton.ReadCallbacks() {
                        @Override
                        public void onUserReadSuccess(User user) {
                            authService.storeCurrentUser(user);
                        }

                        @Override
                        public void onUserReadFail(String errorMsg) {
                            authService.clearCurrentUser();
                        }
                    });

                } else {
                    callbacks.onLoggedOut();
                    authService.clearCurrentUser();
                }
            }
        });
    }

}
