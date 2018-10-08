package ru.aakumykov.me.mvp;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.security.cert.Extension;

class LoginPresenter implements iLogin.Presenter, iLoginCallbacks {

    private final static String TAG = "LoginPresenter";
    private iLogin.View view;
    private iLogin.Model model;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    // TODO: валидация форм

    LoginPresenter() {
        Log.d(TAG, "new LoginPresenter()");
        model = LoginModel.getInstance(this);
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

    @Override
    public void onAuthSuccess() {
        Log.d(TAG, "onAuthSuccess()");

        view.hideProgressBar();
        view.enableLoginForm();

        if (task.isSuccessful()) {
            view.showInfo(R.string.login_success);
        }
        else if (task.isCanceled()) {
            view.showError(R.string.login_canceled);
        }
        else {
            view.showError(R.string.login_error);
            Exception e = task.getException();
            if (null != e) e.printStackTrace();
        }
    }
}
