package ru.aakumykov.me.mvp;

import android.util.Log;

class LoginPresenter implements iLogin.Presenter, iLogin.LoginCallbacks {

    private final static String TAG = "myLog";
    private iLogin.View view;
    private iLogin.Model model;

    LoginPresenter() {
        Log.d(TAG, "new LoginPresenter()");
        model = LoginModel.getInstance(this);
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
        String email = view.getEmail();
        String password = view.getPassword();
        view.showProgressBar();
        model.login(email, password);
    }

    @Override
    public void logoutButtonClicked() {
        model.logout();
    }


    @Override
    public void onAuthSuccess() {
        view.hideProgressBar();
        view.showInfo(R.string.login_success);
    }

    @Override
    public void onAuthFail() {
        view.hideProgressBar();
        view.showError(R.string.login_error);
    }

    @Override
    public void onAuthCancel() {
        view.hideProgressBar();
        view.showWarning(R.string.login_canceled);
    }

    @Override
    public void onLogoutSuccess() {
        view.showInfo(R.string.logout_success);
    }

    @Override
    public void onLogoutFail() {
        view.showError(R.string.logout_error);
    }
}
