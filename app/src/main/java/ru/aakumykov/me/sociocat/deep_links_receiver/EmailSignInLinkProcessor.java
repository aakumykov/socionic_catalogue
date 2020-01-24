package ru.aakumykov.me.sociocat.deep_links_receiver;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.singletons.AuthSingleton;
import ru.aakumykov.me.sociocat.singletons.iAuthSingleton;

class EmailSignInLinkProcessor {

    public static void process(Context context, String deepLink) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES_EMAIL_CHANGE, Context.MODE_PRIVATE);
        String storedEmail = sharedPreferences.getString(Constants.KEY_STORED_EMAIL, "");

        AuthSingleton.signInWithEmailLink(storedEmail, deepLink, new iAuthSingleton.EmailLinkSignInCallbacks() {
            @Override
            public void onEmailLinkSignInSuccess() {

            }

            @Override
            public void onEmailLinkSignInError(String errorMsg) {

            }
        });
    }
}
