package ru.aakumykov.me.sociocat.login;

import android.content.Intent;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_edit.CardEdit_View;
import ru.aakumykov.me.sociocat.interfaces.iAuthSingleton;
import ru.aakumykov.me.sociocat.interfaces.iUsersSingleton;
import ru.aakumykov.me.sociocat.models.User;
import ru.aakumykov.me.sociocat.services.AuthSingleton;
import ru.aakumykov.me.sociocat.services.UsersSingleton;

public class Login_Presenter implements
        iLogin.Presenter,
        iAuthSingleton.LoginCallbacks
{
    private final static String TAG = "Login_Presenter";
    private iLogin.View view;
    private iAuthSingleton authService = AuthSingleton.getInstance();
    private iUsersSingleton usersService = UsersSingleton.getInstance();
    private String intentAction;


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
    public void processInputIntent(@Nullable Intent intent) {

        if (null != intent) {

            String action = intent.getAction() + "";
            this.intentAction = action;

            switch (action) {
                case Constants.ACTION_TRY_NEW_PASSWORD:
                    view.showInfoMsg(R.string.LOGIN_try_new_password);
                    break;

                default:
                    break;
            }
        }
    }

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
    public void onLoginSuccess(final String userId) {

        usersService.getUserById(userId, new iUsersSingleton.ReadCallbacks() {
            @Override
            public void onUserReadSuccess(User user) {

                if (!user.isEmailVerified()) {
                    view.notifyToConfirmEmail(userId);
                    return;
                }

                if (intentAction.equals(Constants.ACTION_CREATE)) {
                    view.goCreateCard();
                    return;
                }

                view.hideProgressBar();
                view.finishLogin(false);
            }

            @Override
            public void onUserReadFail(String errorMsg) {
                view.showToast(R.string.LOGIN_login_error);
                Log.e(TAG, errorMsg);
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


    // Внутренние методы
    private void goCardCreation() {
        Intent intent = new Intent(view.getAppContext(), CardEdit_View.class);
        intent.setAction(Constants.ACTION_CREATE);
        view.startMyActivity(intent);
    }
}
