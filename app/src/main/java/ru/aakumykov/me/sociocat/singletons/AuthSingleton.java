package ru.aakumykov.me.sociocat.singletons;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;

import java.util.List;

import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.aakumykov.me.sociocat.AppConfig;
import ru.aakumykov.me.sociocat.DeepLink_Constants;
import ru.aakumykov.me.sociocat.FirebaseConstants;
import ru.aakumykov.me.sociocat.PackageConstants;
import ru.aakumykov.me.sociocat.utils.MyUtils;

// TODO: разобраться с гостевым пользователем

public class AuthSingleton implements iAuthSingleton
{
    // Шаблон одиночка (начало)
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
    // Шаблон одиночка (конец)


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

    public static void checkPassword(
            @NonNull String email,
            @NonNull String password,
            @NonNull CheckPasswordCallbacks callbacks
    )
    {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (null == firebaseUser) {
            callbacks.onUserCredentialsNotOk("Firebase user is null");
            return;
        }

        AuthCredential authCredential = EmailAuthProvider.getCredential(email, password);

        firebaseUser.reauthenticate(authCredential)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callbacks.onUserCredentialsOk();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callbacks.onUserCredentialsNotOk(e.getMessage());
                        MyUtils.printError(TAG, e);
                    }
                });

    }

    public static void sendEmailChangeConfirmationLink(String userId, String emailAddress, SendSignInLinkCallbacks callbacks) {

        sendSignInLinkToEmail(userId, emailAddress, DeepLink_Constants.ACTION_CHANGE_EMAIL, callbacks);
    }

    public static void sendSignInLinkToEmail(String userId, String emailAddress, @Nullable String action, SendSignInLinkCallbacks callbacks) {

        String continueURL = DeepLink_Constants.URL_BASE +
                DeepLink_Constants.CONFIRM_EMAIL_PATH +
                "?" +
                DeepLink_Constants.KEY_USER_ID + "=" + userId +
                "&" +
                DeepLink_Constants.KEY_EMAIL + "=" + emailAddress;

        if (null != action)
            continueURL += "&" + DeepLink_Constants.KEY_ACTION + "=" + action;

        ActionCodeSettings actionCodeSettings =
                ActionCodeSettings.newBuilder()
                        .setUrl(continueURL)
                        .setHandleCodeInApp(true)
                        .setAndroidPackageName(
                                PackageConstants.PACKAGE_NAME,
                                true, // Установить программу в случае её отсутствия
                                PackageConstants.VERSION_NAME // Минимальная версия устанавливаемой программы
                        )
                        .build();

        FirebaseAuth.getInstance().sendSignInLinkToEmail(emailAddress, actionCodeSettings)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callbacks.onSignInLinkSendSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callbacks.onSignInLinkSendFail(e.getMessage());
                        MyUtils.printError(TAG, e);
                    }
                });

    }

    public static void resetPasswordEmail(String email, final ResetPasswordCallbacks callbacks) {

        ActionCodeSettings actionCodeSettings =
                ActionCodeSettings.newBuilder()
                        .setUrl(DeepLink_Constants.URL_BASE + DeepLink_Constants.PASSWORD_RESET_PATH)
                        .setHandleCodeInApp(false)
                        .setAndroidPackageName(
                                PackageConstants.PACKAGE_NAME,
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

    public static boolean isEmailSignInLink(@Nullable String deepLink) {
        if (null == deepLink)
            return false;

        return firebaseAuth.isSignInWithEmailLink(deepLink);
    }

    public static boolean isPasswordResetLink(@Nullable String deepLink) {
        if (null == deepLink)
            return false;

        Uri uri = Uri.parse(deepLink);

        //String path = uri.getPath();

        return DeepLink_Constants.PASSWORD_RESET_PATH.equals(uri.getPath());
    }

    public static void loginWithEmailAndPassword(String email, String password, iAuthSingleton.LoginCallbacks callbacks) {

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {

                    FirebaseUser firebaseUser = authResult.getUser();

                    if (null != firebaseUser)
                        callbacks.onLoginSuccess(firebaseUser.getUid());
                    else
                        callbacks.onLoginError("FirebaseUser is null");
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    String errorCode = "EMPTY_ERROR_CODE";

                    if (e instanceof FirebaseAuthException) {
                        FirebaseAuthException firebaseAuthException = (FirebaseAuthException) e;
                        errorCode = firebaseAuthException.getErrorCode() + "";
                    }
                    else if (e instanceof FirebaseTooManyRequestsException) {
                        errorCode = FirebaseConstants.TOO_MANY_LOGIN_ATTEMPTS;
                    }

                    switch (errorCode) {
                        case FirebaseConstants.ERROR_WRONG_PASSWORD:
                        case FirebaseConstants.ERROR_USER_NOT_FOUND:
                            callbacks.onWrongCredentialsError();
                            break;

                        case FirebaseConstants.TOO_MANY_LOGIN_ATTEMPTS:
                            callbacks.onTooManyLoginAttempts();
                            break;

                        default:
                            callbacks.onLoginError(e.getMessage());
                            MyUtils.printError(TAG, e);
                            break;
                    }
                }
            });
    }

    public static void loginWithGoogle(@Nullable GoogleSignInAccount googleSignInAccount, iAuthSingleton.LoginCallbacks callbacks) {
        if (null == googleSignInAccount) {
            callbacks.onLoginError("GoogleSignInAccount is null");
            return;
        }

        AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);

        firebaseAuth.signInWithCredential(authCredential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        FirebaseUser firebaseUser = authResult.getUser();
                        if(null != firebaseUser) {
                            String userId = firebaseUser.getUid();
                            callbacks.onLoginSuccess(userId);
                        }
                        else {
                            callbacks.onLoginError("FirebaseUser is null");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callbacks.onLoginError(e.getMessage());
                        MyUtils.printError(TAG, e);
                    }
                });
    }

    public static void signOut() {
        firebaseAuth.signOut();
    }

    public static void loginWithEmailLink(String referenceEmail, String emailSignInLink, iAuthSingleton.EmailLinkSignInCallbacks callbacks) {

        firebaseAuth.signInWithEmailLink(referenceEmail, emailSignInLink)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        FirebaseUser firebaseUser = authResult.getUser();
                        if (null != firebaseUser) {
                            String userId = firebaseUser.getUid();
                            callbacks.onLoginSuccess(userId);
                        }
                        else
                            callbacks.onLoginError("FirebaseUser is null");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        FirebaseAuthException firebaseAuthException = (FirebaseAuthException) e;
                        String errorCode = firebaseAuthException.getErrorCode();

                        if (FirebaseConstants.ERROR_INVALID_ACTION_CODE.equals(errorCode))
                            callbacks.onLoginLinkHasExpired();
                        else
                            callbacks.onLoginError(e.getMessage());

                        MyUtils.printError(TAG, e);
                    }
                });

    }


    // Свойства
    private final static String TAG = "AuthSingleton";
    private static FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    public static void checkEmailExists(@Nullable String email, iAuthSingleton.CheckEmailExistsCallbacks callbacks) {

        if (null == email) {
            callbacks.onEmailCheckError("Email cannot be null");
            return;
        }

        firebaseAuth.fetchSignInMethodsForEmail(email)
                .addOnSuccessListener(new OnSuccessListener<SignInMethodQueryResult>() {
                    @Override
                    public void onSuccess(SignInMethodQueryResult signInMethodQueryResult) {
                        List<String> signInMethodsList = signInMethodQueryResult.getSignInMethods();

                        if (null == signInMethodsList) {
                            callbacks.onEmailNotExists();
                            return;
                        }

                        if (0 == signInMethodsList.size()) {
                            callbacks.onEmailNotExists();
                            return;
                        }

                        callbacks.onEmailExists();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callbacks.onEmailNotExists();
                    }
                });
    }

    public static void changePassword(@NonNull String password, iAuthSingleton.ChangePasswordCallbacks callbacks) {

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (null == firebaseUser) {
            callbacks.onChangePasswordError("FirebaseUser is null");
            return;
        }

        firebaseUser.updatePassword(password)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callbacks.onChangePasswordSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callbacks.onChangePasswordError(e.getMessage());
                        MyUtils.printError(TAG, e);
                    }
                });
    }

    public static String emailOfCurrentUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        return (null != firebaseUser) ? firebaseUser.getEmail() : null;
    }


    // Внутренние интерфейсы
    private interface CustomTokenAPI {
        @GET(AppConfig.CREATE_CUSTOM_TOKEN_PATH)
        retrofit2.Call<String> getCustomToken(@Query(AppConfig.CREATE_CUSTOM_TOKEN_PARAMETER_NAME) String tokenBase);
    }


    // Внутренние методы
    private static CustomTokenAPI getCustomTokenAPI() {
        return new Retrofit.Builder()
                .baseUrl(AppConfig.CREATE_CUSTOM_TOKEN_BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()
                .create(CustomTokenAPI.class);
    }


    // Классы исключений
    public static class LinkExpiredException extends AuthSingletonException {
        public LinkExpiredException(String message) {
            super(message);
        }
    }

    public static class AuthSingletonException extends Exception {
        public AuthSingletonException(String message) {
            super(message);
        }
    }

}

