package ru.aakumykov.me.sociocat;

import android.app.Application;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;

public class MyApp extends Application {

    private final static String TAG = "MyApp";

    @Override public void onCreate() {
        super.onCreate();

        FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                String uid = firebaseAuth.getUid();

                //Log.d(TAG, "firebaseAuth: "+firebaseAuth);
                //Log.d(TAG, "firebaseUser: "+firebaseUser);
                Log.d(TAG, "uid: "+uid);

            }
        });
    }
}
