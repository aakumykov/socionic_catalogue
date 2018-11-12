package ru.aakumykov.me.mvp.services;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ru.aakumykov.me.mvp.interfaces.iAuthStateListener;

// TODO: как обрабатывать reauthenticate ?

public class AuthStateListener implements iAuthStateListener {

    private final static String TAG = "AuthStateListener";

    public AuthStateListener(final iAuthStateListener.StateChangeCallbacks callbacks) {
        Log.d(TAG, "new AuthStateListener()");

        FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (null == firebaseUser) {
                    callbacks.onLoggedOut();
                } else {
                    callbacks.onLoggedIn();
                }
            }
        });
    }

}
