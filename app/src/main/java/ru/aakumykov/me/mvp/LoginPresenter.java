package ru.aakumykov.me.mvp;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

class LoginPresenter implements iLogin.Presenter {

    private final static String TAG = "myLog";
    private iLogin.View view;
    private iLogin.Model model;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    LoginPresenter() {
        Log.d(TAG, "=LoginPresenter()=");
        model = new LoginModel();
        firebaseAuth = FirebaseAuth.getInstance();
    }


    @Override
    public void linkView(iLogin.View view) {
        Log.d(TAG, "=linkView()=");
        this.view = view;
    }
    @Override
    public void unlinkView() {
        Log.d(TAG, "=unlinkView()=");
        this.view = null;
    }


    @Override
    public void loginButtonClicked() {
        login();
    }

    public void logoutButtonClicked() {
        logout();
    }


    private void login() {
        Log.d(TAG, "login()");

        String email = view.getEmail();
        String password = view.getPassword();

        view.disableLoginForm();
        view.showProgressBar();
        view.showInfo(R.string.logging_in);

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    Log.d(TAG, "LOGIN SUCCESS");
                    firebaseUser = firebaseAuth.getCurrentUser();
                    view.hideProgressBar();
                    view.showInfo(R.string.login_success);
                    view.enableLoginForm();
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "LOGIN FAILED");
                    view.hideProgressBar();
                    view.showError(R.string.login_error);
                    view.enableLoginForm();
                }
            })
            .addOnCanceledListener(new OnCanceledListener() {
                @Override
                public void onCanceled() {
                    Log.d(TAG, "LOGIN CANCELLED");
                    view.hideProgressBar();
                    view.showError(R.string.login_canceled);
                    view.enableLoginForm();
                }
            });
    }

    private void logout() {
        Log.d(TAG, "logout()");

        firebaseAuth.signOut();
        firebaseUser = firebaseAuth.getCurrentUser();

        if (null == firebaseUser) {
            view.showInfo(R.string.logout_success);
        } else {
            view.showError(R.string.logout_error);
        }
    }
}
