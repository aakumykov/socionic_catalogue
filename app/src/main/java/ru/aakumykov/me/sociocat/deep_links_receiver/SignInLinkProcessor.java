package ru.aakumykov.me.sociocat.deep_links_receiver;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
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
import ru.aakumykov.me.sociocat.utils.MyUtils;

class SignInLinkProcessor {

    // Внешние методы
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
                String continueUrl = Uri.parse(deepLink).getQueryParameter("continueUrl");
                if (null == continueUrl) {
                    callbacks.onLinkSignInSuccess();
                    return;
                }

                try {
                    ContinueUrlProcessor.process(continueUrl);
                }
                catch (ContinueUrlProcessor.ContinueUrlProcessorException e) {
                    callbacks.onActionProcessError(e.getMessage());
                    MyUtils.printError(TAG, e);
                }
            }

            @Override
            public void onEmailLinkSignInError(String errorMsg) {
                callbacks.onLinkSignInFailed(errorMsg);
            }
        });
    }


    // Внутренние методы
    private static void processContinueUrl(@NonNull String continueUrl) {
        //Uri continueURI = Uri.parse(continueUrl)
    }

    // Интерфейсы
    public interface SignInLinkProcessorCallbacks {
        void onLinkSignInSuccess();
        void onLinkSignInFailed(String errorMsg);
        void onActionProcessError(String errorMsg);
    }

    // Свойства
    private static final String TAG = "SignInLinkProcessor";
}
