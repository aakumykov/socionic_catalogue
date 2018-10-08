package ru.aakumykov.me.mvp;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

// TODO: одиночка с синхронизацией!
class LoginModel implements iLogin.Model {

    private final static String TAG = "LoginModel";
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private iLoginCallbacks loginCallbacks;

    /* Одиночка, начало */
    private volatile static LoginModel ourInstance;

    public static synchronized LoginModel getInstance(iLoginCallbacks loginCallbacks) {
        synchronized (LoginModel.class) {
            if (null == ourInstance) {
                ourInstance = new LoginModel(loginCallbacks);
            }
        }
        return ourInstance;
    }

    private LoginModel(iLoginCallbacks loginCallbacks) {
        Log.d(TAG, "new LoginModel()");
        this.loginCallbacks = loginCallbacks;
    }
    /* Одиночка, конец */


    @Override
    public void login(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // TODO: обработка разных результатов авторизации
                        loginCallbacks.onAuthSuccess();
                    }
                });
    }
}
