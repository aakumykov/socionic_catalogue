package ru.aakumykov.me.mvp;

import android.util.Log;

class LoginPresenter implements iLogin.Presenter {

    private final static String TAG = "myLog";
    private iLogin.View view;
    private iLogin.Model model;

    LoginPresenter() {
        Log.d(TAG, "=LoginPresenter()=");
        this.model = new LoginModel();
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
        doLogin();
    }


    private void doLogin() {
        Log.d(TAG, "doLogin()");

        view.showInfo("Осуществляется вход");
        view.disableLoginButton();

//        try {
//            TimeUnit.SECONDS.sleep(2);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        view.hideInfo();
//        view.enableLoginButton();
    }
}
