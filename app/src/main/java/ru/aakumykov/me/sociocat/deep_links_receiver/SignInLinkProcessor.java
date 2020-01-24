package ru.aakumykov.me.sociocat.deep_links_receiver;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.singletons.AuthSingleton;
import ru.aakumykov.me.sociocat.singletons.iAuthSingleton;

class SignInLinkProcessor {

    public static void process(Context context, String deepLink, SignInLinkProcessorCallbacks callbacks) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES_EMAIL_CHANGE, Context.MODE_PRIVATE);
        String storedEmail = sharedPreferences.getString(Constants.KEY_STORED_EMAIL, "");

        if (TextUtils.isEmpty(storedEmail)) {
            callbacks.onLinkSignInFailed("There is no stored email to complete sign in process");
            return;
        }

        AuthSingleton.signInWithEmailLink(storedEmail, deepLink, new iAuthSingleton.EmailLinkSignInCallbacks() {
            @Override
            public void onEmailLinkSignInSuccess() {
                callbacks.onLinkSignInSuccess();
            }

            @Override
            public void onEmailLinkSignInError(String errorMsg) {
                callbacks.onLinkSignInFailed(errorMsg);
            }
        });
    }

    public interface SignInLinkProcessorCallbacks {
        void onLinkSignInSuccess();
        void onLinkSignInFailed(String errorMsg);
    }

    private static final String TAG = "SignInLinkProcessor";
}
