package ru.aakumykov.me.sociocat.utils.auth;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import ru.aakumykov.me.sociocat.AuthConfig;

public final class GoogleAuthHelper {

    private static final String TAG = GoogleAuthHelper.class.getSimpleName();

    // Внешние методы
    public static Intent getSignInIntent(Context context) {
        return signInClient(context).getSignInIntent();
    }

    public static void logout(Context context) {
        signInClient(context).signOut();
    }

    public static void processGoogleLoginResult(@Nullable Intent data, iGoogleLoginCallbacks callbacks) {

        if (null == data) {
            callbacks.onGoogleLoginError("Intent is null");
            return;
        }

        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        if (!task.isSuccessful()) {
            ApiException apiException = (ApiException) task.getException();

            String errorMsg;
            if (null != apiException) {
                int errorCode = apiException.getStatusCode();
                String errorCodeDescription = GoogleSignInStatusCodes.getStatusCodeString(errorCode);
                errorMsg = errorCode + " (" + errorCodeDescription + ")";
            }
            else {
                errorMsg = "Unknown auth error";
            }

            Log.e(TAG, errorMsg, apiException);

            callbacks.onGoogleLoginError(errorMsg);
            return;
        }

        try {
            GoogleSignInAccount googleSignInAccount = task.getResult(ApiException.class);
            callbacks.onGoogleLoginSuccess(googleSignInAccount);
        }
        catch (ApiException apiException) {
            callbacks.onGoogleLoginError(apiException.getMessage());
            Log.e(TAG, apiException.getMessage(), apiException);
        }
    }

    // Внутренние методы
    private static GoogleSignInClient signInClient(Context context) {
        return GoogleSignIn.getClient(context, googleSignInOptions);
    }

    // Интерфейсы
    public interface iGoogleLoginCallbacks {
        void onGoogleLoginSuccess(@NonNull GoogleSignInAccount googleSignInAccount);
        void onGoogleLoginError(String errorMsg);
    }

    // Свойства
    private static GoogleSignInOptions googleSignInOptions =
            new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(AuthConfig.GOOGLE_WEB_CLIENT_ID)
                    //.requestProfile()
                    .requestEmail()
                    .build();

}
