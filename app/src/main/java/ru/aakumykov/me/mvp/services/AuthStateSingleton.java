package ru.aakumykov.me.mvp.services;

import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ru.aakumykov.me.mvp.interfaces.iAuthStateSingleton;

public class AuthStateSingleton implements iAuthStateSingleton {

    /* Одиночка */
    private static volatile AuthStateSingleton ourInstance;
    public synchronized static AuthStateSingleton getInstance() {
        synchronized (AuthStateSingleton.class) {
            if (null == ourInstance) ourInstance = new AuthStateSingleton();
            return ourInstance;
        }
    }
    private AuthStateSingleton() {
        this.firebaseAuth = FirebaseAuth.getInstance();
    }
    /* Одиночка */

    private final static String TAG = "AuthStateSingleton";
    private FirebaseAuth firebaseAuth;
    private boolean firstRun;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    public void registerListener(final iAuthStateSingletonCallbacks callbacks) {

        firstRun = true;

        this.authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (!firstRun) {
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    if (null == firebaseUser) {
                        callbacks.onAuthOut();
                    } else {
                        callbacks.onAuthIn();
                    }
                }

                firstRun = false;
            }
        };

        firebaseAuth.addAuthStateListener(this.authStateListener);
    }

    @Override
    public void unregiserListener() {
        firebaseAuth.removeAuthStateListener(this.authStateListener);
    }
}
