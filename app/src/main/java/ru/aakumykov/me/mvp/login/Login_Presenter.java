package ru.aakumykov.me.mvp.login;

import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.interfaces.iAuthSingleton;
import ru.aakumykov.me.mvp.interfaces.iCardsSingleton;
import ru.aakumykov.me.mvp.services.AuthSingleton;
import ru.aakumykov.me.mvp.services.CardsSingleton;

public class Login_Presenter implements
        iLogin.Presenter,
        iAuthSingleton.LoginCallbacks
{
    private final static String TAG = "Login_Presenter";
    private iLogin.View view;
    private iCardsSingleton cardsService = CardsSingleton.getInstance();
    private iAuthSingleton authService = AuthSingleton.getInstance();

    // Обязательные методы
    @Override
    public void linkView(iLogin.View view) {
        this.view = view;
    }
    @Override
    public void unlinkView() {
        this.view = null;
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
