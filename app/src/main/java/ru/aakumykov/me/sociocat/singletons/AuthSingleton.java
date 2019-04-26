package ru.aakumykov.me.sociocat.singletons;

import androidx.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.interfaces.iAuthSingleton;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.models.User;

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
    private FirebaseAuth firebaseAuth;


    // Интерфейсные методы
    // Регистрация, вход, выход
//    @Override
//    public void registerWithEmail(String email, String password,
//            final iAuthSingleton.RegisterCallbacks callbacks) throws Exception
//    {
//        Log.d(TAG, "registerWithEmail("+email+", ***)");
//
//        firebaseAuth.createUserWithEmailAndPassword(email, password)
//                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
//                    @Override
//                    public void onSuccess(AuthResult authResult) {
//                        callbacks.onRegSucsess(authResult.getUser().getUid(), authResult.getUser().getEmail());
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        callbacks.onRegFail(e.getMessage());
//                        e.printStackTrace();
//                    }
//                });
//    }

    @Override
    public void login(String email, String password, final LoginCallbacks callbacks) throws Exception {

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        callbacks.onLoginSuccess(authResult.getUser().getUid());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callbacks.onLoginFail(e.getMessage());
                        e.printStackTrace();
                    }
                });
    }

    @Override
    public void cancelLogin() {
        firebaseAuth.signOut();
    }

    @Override
    public void logout() {
        firebaseAuth.signOut();
    }


//    @Override
//    public void restoreCurrentUser(final iAuthSingleton.UserRestoreCallbacks callbacks) {
//        usersSingleton.getUserById(currentUserId(), new iUsersSingleton.ReadCallbacks() {
//            @Override
//            public void onUserReadSuccess(User user) {
//                storeCurrentUser(user);
//                callbacks.onUserRestoreSuccess();
//            }
//
//            @Override
//            public void onUserReadFail(String errorMsg) {
//                callbacks.onUserRestoreFail(errorMsg);
//            }
//        });
//    }


    // Параметры текущего пользователя
//    @Override
//    public User currentUser() {
//        return this.currentUser;
//    }

    @Override
    public String currentUserId() {
        return firebaseAuth.getUid();
    }

//    @Override
//    public String currentUserName() {
//        return currentUser.getName();
//    }

    @Override
    public boolean isUserLoggedIn() {
        return null != firebaseAuth.getCurrentUser();
    }

    @Override
    public boolean isCardOwner(Card card) {
        return card.getUserId().equals(currentUserId());
    }

/*
    @Override
    public void sendSignInLinkToEmail(String email, final SendSignInLinkToEmailCallbacks callbacks) {

        String url = "http://sociocat.example.org/verify";

        ActionCodeSettings actionCodeSettings = ActionCodeSettings.newBuilder()
                .setUrl(url)
                .setAndroidPackageName(Constants.PACKAGE_NAME, true, null)
                .setHandleCodeInApp(true)
                .build();

        firebaseAuth.sendSignInLinkToEmail(email, actionCodeSettings)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callbacks.onSendSignInLinkToEmailSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callbacks.onSendSignInLinkToEmailFail(e.getMessage());
                        e.printStackTrace();
                    }
                });
    }
*/

/*
    @Override
    public void sendEmailVerificationLink(String packageName, final SendEmailVerificationLinkCallbacks callbacks) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        String url = "http://sociocat.example.org/verify?uid=" + user.getUid();

        ActionCodeSettings actionCodeSettings = ActionCodeSettings.newBuilder()
                .setUrl(url)
                // The default for this is populated with the current android package name.
                .setAndroidPackageName(packageName, true, null)
                .setHandleCodeInApp(false)
                .build();

        user.sendEmailVerification(actionCodeSettings)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        logout();
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callbacks.onEmailVerificationLinkSendSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callbacks.onEmailVerificationLinkSendFail(e.getMessage());
                        e.printStackTrace();
                    }
                });

    }
*/

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

    // Служебные
/*
    @Override
    public void storeCurrentUser(final User user) {
        // TODO: проверять на null, бросать исключение?
        this.currentUser = user;
    }
*/

/*
    @Override
    public void clearCurrentUser() {
        this.currentUser = null;
    }
*/

}

