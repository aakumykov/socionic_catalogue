package ru.aakumykov.me.mvp;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

class LoginModel implements iLogin.Model {

    private final static String TAG = "myLog";
    private static volatile LoginModel ourInstance;
    private iLogin.LoginCallbacks loginCallbacks;
    private FirebaseAuth firebaseAuth;

    static synchronized LoginModel getInstance(iLogin.LoginCallbacks loginCallbacks) {
        Log.d(TAG, "* LoginModel#getInstance() *");
        synchronized (LoginModel.class) {
            if (null == ourInstance) {
                ourInstance = new LoginModel(loginCallbacks);
            }
        }
        return ourInstance;
    }

    private LoginModel(iLogin.LoginCallbacks loginCallbacks) {
        Log.d(TAG, "* new LoginModel() *");
        this.loginCallbacks = loginCallbacks;
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void login(String email, String password) {
        Log.d(TAG, "LoginModel.login("+email+", "+password+")");

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        loginCallbacks.onAuthSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loginCallbacks.onAuthFail();
                        e.printStackTrace();
                    }
                })
                .addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        loginCallbacks.onAuthCancel();
                    }
                });
    }

    @Override
    public void logout() {
        Log.d(TAG, "LoginModel.logout()");

        firebaseAuth.signOut();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (null == firebaseUser) {
            loginCallbacks.onLogoutSuccess();
        } else {
            loginCallbacks.onLogoutFail();
        }
    }
}
