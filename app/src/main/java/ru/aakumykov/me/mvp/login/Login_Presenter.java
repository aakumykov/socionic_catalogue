package ru.aakumykov.me.mvp.login;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.interfaces.iAuthSingleton;
import ru.aakumykov.me.mvp.interfaces.iUsersSingleton;
import ru.aakumykov.me.mvp.models.User;
import ru.aakumykov.me.mvp.services.AuthSingleton;
import ru.aakumykov.me.mvp.services.UsersSingleton;

public class Login_Presenter implements
        iLogin.Presenter,
        iAuthSingleton.LoginCallbacks
{
    private final static String TAG = "Login_Presenter";
    private iLogin.View view;
    private iAuthSingleton authService = AuthSingleton.getInstance();
    private iUsersSingleton usersService = UsersSingleton.getInstance();

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

    @Override
    public void processInputIntent(@Nullable Intent intent) {
        if (null != intent) {
            String action = intent.getAction() + "";
            if (action.equals(Constants.ACTION_LOGIN_FOR_COMMENT)) {

            }
        }
    }


    // Методы обратного вызова
    @Override
    public void onLoginSuccess(final String userId) {

        usersService.getUserById(userId, new iUsersSingleton.ReadCallbacks() {
            @Override
            public void onUserReadSuccess(User user) {

                if (user.isEmailVerified()) {
                    view.hideProgressBar();
                    view.showToast(R.string.LOGIN_login_success);
                    view.finishLogin(false);
                } else {
                    authService.logout();
                    view.notifyToConfirmEmail(userId);
                }
            }

            @Override
            public void onUserReadFail(String errorMsg) {
                view.showErrorMsg(R.string.LOGIN_login_error, errorMsg);
                authService.logout();
            }
        });
    }

    @Override
    public void onLoginFail(String errorMsg) {
        view.hideProgressBar();
        view.enableForm();
        view.showErrorMsg(errorMsg);
    }
}
