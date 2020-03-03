package ru.aakumykov.me.sociocat.singletons;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;

import ru.aakumykov.me.sociocat.AuthConfig;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.login.iLogin;

public final class GoogleAuthClient {

    // Интерфейсы
    public interface iGoogleLoginCallbacks {
        void onGoogleLoginSuccess(GoogleSignInAccount googleSignInAccount);
        void onGoogleLoginError(String errorMsg);
    }

    // Свойства
    private static GoogleSignInOptions googleSignInOptions =
            new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(AuthConfig.GOOGLE_WEB_CLIENT_ID)
            .requestProfile()
            .requestEmail()
            .build();


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


    // Шаблон Единоличник
    private static volatile GoogleAuthClient ourInstance;
    public synchronized static GoogleAuthClient getInstance() {
        synchronized (GoogleAuthClient.class) {
            if (null == ourInstance) ourInstance = new GoogleAuthClient();
            return ourInstance;
        }
    }
    private GoogleAuthClient() {

    }
    // Шаблон Единоличник


}
