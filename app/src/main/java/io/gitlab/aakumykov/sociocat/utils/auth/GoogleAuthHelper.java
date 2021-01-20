package io.gitlab.aakumykov.sociocat.utils.auth;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;

import io.gitlab.aakumykov.sociocat.AuthConfig;

public final class GoogleAuthHelper {

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
            callbacks.onGoogleLoginError("Google auth is not successful");
            return;
        }

        GoogleSignInAccount googleSignInAccount = task.getResult();
        if (null == googleSignInAccount) {
            callbacks.onGoogleLoginError("Google sign in account is null");
            return;
        }

        callbacks.onGoogleLoginSuccess(googleSignInAccount);
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
                    .requestProfile()
                    .requestEmail()
                    .build();

}
