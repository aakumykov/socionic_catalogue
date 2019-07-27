package ru.aakumykov.me.sociocat.singletons;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;

import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
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



    // Создание Firebase Custom Token
    public static void createFirebaseCustomToken(String externalToken,
                                                 iAuthSingleton.CreateFirebaseCustomToken_Callbacks callbacks)
    {
        AuthSingleton.getCustomTokenAPI()
                .getCustomToken(externalToken)
                .enqueue(new retrofit2.Callback<String>() {
                    @Override
                    public void onResponse(retrofit2.Call<String> call, retrofit2.Response<String> response) {
                        if (response.isSuccessful()) {
                            String result = response.body();
                            callbacks.onCreateFirebaseCustomToken_Success(result);
                        }
                        else {
                            callbacks.onCreateFirebaseCustomToken_Error(response.code() + ": " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<String> call, Throwable t) {
                        callbacks.onCreateFirebaseCustomToken_Error(t.getMessage());
                        t.printStackTrace();
                    }
                });
    }

    private interface CustomTokenAPI {
        @GET(Config.CREATE_CUSTOM_TOKEN_PATH)
        retrofit2.Call<String> getCustomToken(@Query(Config.CREATE_CUSTOM_TOKEN_PARAMETER_NAME) String tokenBase);
    }

    private static CustomTokenAPI getCustomTokenAPI() {
        return new Retrofit.Builder()
                .baseUrl(Config.CREATE_CUSTOM_TOKEN_BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()
                .create(CustomTokenAPI.class);
    }



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

