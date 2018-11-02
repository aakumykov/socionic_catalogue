package ru.aakumykov.me.mvp.login;

import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.interfaces.iAuthService;
import ru.aakumykov.me.mvp.interfaces.iCardsService;

public class Login_Presenter implements
        iLogin.Presenter,
        iAuthService.LoginCallbacks
{
    private final static String TAG = "Register_Presenter";
    private iLogin.View view;
    private iCardsService model;
    private iAuthService authService;


    // Интерфейсные методы
    @Override
    public void doLogin(String email, String password) {
        try {
            authService.login(email, password, this);
        }
        catch (Exception e) {
            view.hideProgressBar();
            view.enableForm();
            view.showErrorMsg(R.string.LOGIN_login_failed, e.getMessage());
        }
    }

    @Override
    public void cancelLogin() {
        authService.cancelLogin();
        view.finishLogin(true);
    }

    // Системные методы
    @Override
    public void linkView(iLogin.View view) {
        this.view = view;
    }
    @Override
    public void unlinkView() {
        this.view = null;
    }

    @Override
    public void linkModel(iCardsService model) {
        this.model = model;
    }
    @Override
    public void unlinkModel() {
        this.model = null;
    }

    @Override
    public void linkAuth(iAuthService authService) {
        this.authService = authService;
    }
    @Override
    public void unlinkAuth() {
        this.authService = null;
    }


    // Методы обратного вызова

    @Override
    public void onLoginSuccess() {
        view.hideProgressBar();
        view.showInfoMsg(R.string.LOGIN_login_success);
        view.finishLogin(false);
    }

    @Override
    public void onLoginFail(String errorMsg) {
        view.hideProgressBar();
        view.enableForm();
        view.showErrorMsg(R.string.LOGIN_login_failed, errorMsg);
    }
}
