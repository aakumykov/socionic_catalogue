package ru.aakumykov.me.sociocat.singletons;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ru.aakumykov.me.sociocat.Config;
import ru.aakumykov.me.sociocat.Constants;

// TODO: разобраться с гостевым пользователем

public class AuthSingleton implements iAuthSingleton
{
    /* Одиночка */
    private static volatile AuthSingleton ourInstance;
    public synchronized static AuthSingleton getInstance() {
        synchronized (AuthSingleton.class) {
            if (null == ourInstance) ourInstance = new AuthSingleton();
            return ourInstance;
        }
    }
    private AuthSingleton() {
        firebaseAuth = FirebaseAuth.getInstance();
        Log.d(TAG, "firebaseAuth: "+firebaseAuth);
    }
    /* Одиночка */

    private final static String TAG = "AuthSingleton";
    private static FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();


    // Статические методы
    public static boolean isLoggedIn() {
        return (null != firebaseAuth.getCurrentUser());
    }

    public static void logout() {
        firebaseAuth.signOut();
    }

    public static String currentUserId() {
        return firebaseAuth.getUid();
    }

    public static void createFirebaseCustomToken(String externalToken, iAuthSingleton.CreateFirebaseCustomToken_Callbacks callbacks) {

        OkHttpClient okHttpClient = new OkHttpClient();

        String url = Config.CUSTOM_ACCESS_TOKEN_CREATE_URL + externalToken;

        Request request = new Request.Builder()
                .url(url)
                .build();

        try {
            Call call = okHttpClient.newCall(request);

            call.enqueue(new Callback() {
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String message = response.message();
                    }
//                    try {
//                        String firebaseCustomAccesToken = response.body().string();
//                        callbacks.onCreateFirebaseCustomToken_Success(firebaseCustomAccesToken);
//                    }
//                    catch (NullPointerException e) {
//                        String errorMsg = e.getMessage();
//                        callbacks.onCreateFirebaseCustomToken_Error(errorMsg);
//                        Log.e(TAG, errorMsg);
//                    }
                }

                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    String errorMessage = e.getMessage();
                    callbacks.onCreateFirebaseCustomToken_Error(errorMessage);
                    Log.e(TAG, errorMessage);
                    e.printStackTrace();
                }
            });
        }
        catch (Exception e) {
            String errorMessage = e.getMessage();
            callbacks.onCreateFirebaseCustomToken_Error(errorMessage);
            Log.e(TAG, errorMessage);
            e.printStackTrace();
        }
    }



    // Динамические методы
    @Override
    public void resetPasswordEmail(String email, final ResetPasswordCallbacks callbacks) {

        ActionCodeSettings actionCodeSettings =
                ActionCodeSettings.newBuilder()
                        .setUrl("https://sociocat.example.org/reset_password?uid="+firebaseAuth.getUid())
                        .setHandleCodeInApp(false)
                        .setAndroidPackageName(
                                Constants.PACKAGE_NAME,
                                true, /* installIfNotAvailable */
                                null    /* minimumVersion */)
                        .build();

        firebaseAuth.sendPasswordResetEmail(email, actionCodeSettings)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callbacks.onEmailSendSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callbacks.onEmailSendFail(e.getMessage());
                        e.printStackTrace();
                    }
                });
    }

}

