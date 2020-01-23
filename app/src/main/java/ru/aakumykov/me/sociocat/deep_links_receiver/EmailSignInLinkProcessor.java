package ru.aakumykov.me.sociocat.deep_links_receiver;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import ru.aakumykov.me.sociocat.Constants;

class EmailSignInLinkProcessor {

    private static FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    public static void process(Context context, String deepLink) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES_EMAIL_CHANGE, Context.MODE_PRIVATE);
        String storedEmail = sharedPreferences.getString(Constants.KEY_STORED_EMAIL, "");

        firebaseAuth.signInWithEmailLink(storedEmail, deepLink)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }
}
