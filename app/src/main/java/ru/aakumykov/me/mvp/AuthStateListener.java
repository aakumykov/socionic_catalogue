package ru.aakumykov.me.mvp;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ru.aakumykov.me.mvp.interfaces.iAuthStateListener;


public class AuthStateListener implements iAuthStateListener {

    private final static String TAG = "AuthStateListener";
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    public AuthStateListener(/*iAuthStateListener.StateChangeCallbacks callbacks*/) {
        Log.d(TAG, "new AuthStateListener()");

        firebaseAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                Log.d(TAG, "onAuthStateChanged(), "+firebaseAuth);
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
            }
        });
    }

}
