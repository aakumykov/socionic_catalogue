package ru.aakumykov.me.mvp.login;

import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.interfaces.iAuthSingleton;
import ru.aakumykov.me.mvp.interfaces.iCardsSingleton;

public class Login_Presenter implements
        iLogin.Presenter,
        iAuthSingleton.LoginCallbacks
{
    private final static String TAG = "Login_Presenter";
    private iLogin.View view;
    private iCardsSingleton model;
    private iAuthSingleton authService;


    Login_Presenter() {
//        iAuthStateListener authStateListener = new AuthStateListener();
    }


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
            e.printStackTrace();
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
    public void linkCardsService(iCardsSingleton model) {
        this.model = model;
    }
    @Override
    public void unlinkCardsService() {
        this.model = null;
    }

    @Override
    public void linkAuth(iAuthSingleton authService) {
        this.authService = authService;
    }
    @Override
    public void unlinkAuthService() {
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
