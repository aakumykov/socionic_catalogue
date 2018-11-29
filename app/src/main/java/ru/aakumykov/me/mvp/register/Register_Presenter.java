package ru.aakumykov.me.mvp.register;

import android.util.Log;

import java.util.concurrent.TimeUnit;

import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.interfaces.iAuthSingleton;
import ru.aakumykov.me.mvp.interfaces.iUsersSingleton;
import ru.aakumykov.me.mvp.models.User;
import ru.aakumykov.me.mvp.services.AuthSingleton;
import ru.aakumykov.me.mvp.services.UsersSingleton;

// TODO: проверять имя пользователя

public class Register_Presenter implements
        iRegister.Presenter,
        iAuthSingleton.RegisterCallbacks,
        iUsersSingleton.CreateCallbacks
{
    private final static String TAG = "Register_Presenter";
    private iAuthSingleton authService;
    private iUsersSingleton usersService;
    private iRegister.View view;

    Register_Presenter() {
        authService = AuthSingleton.getInstance();
        usersService = UsersSingleton.getInstance();
    }

    // Интерфейсные методы
    @Override
    public void regUserWithEmail(String email, String password) {
        Log.d(TAG, "regUserWithEmail()");

        view.showProgressBar();

        try {
            authService.registerWithEmail(email, password, this);
        }
        catch (Exception e) {
            view.hideProgressBar();
            view.enableForm();
//            view.showErrorMsg(R.string.REGISTER_registration_failed, e.getMessage());
            view.showErrorMsg(e.getMessage());
            e.printStackTrace();
        }
    }


    // Системные методы
    @Override
    public void linkView(iRegister.View view) {
        this.view = view;
    }
    @Override
    public void unlinkView() {
        this.view = null;
    }


    // Коллбеки
    @Override
    public void onRegSucsess(String userId, String email) {

        view.showInfoMsg(R.string.REGISTER_crating_new_user);

        usersService.createUser(userId, email, new iUsersSingleton.CreateCallbacks() {
            @Override
            public void onUserCreateSuccess(User user) {
                // TODO: внимательно с этим методом
                authService.storeCurrentUser(user);
                view.goUserEditPage(user);
            }

            @Override
            public void onUserCreateFail(String errorMsg) {
                view.hideProgressBar();
                view.showErrorMsg(R.string.REGISTER_error_creating_user, errorMsg);
//                onRegFail(errorMsg);
            }
        });
    }

    @Override
    public void onRegFail(String errorMessage) {
        Log.d(TAG, "onRegFail(), "+errorMessage);
        view.hideProgressBar();
//        view.showErrorMsg(R.string.REGISTER_registration_failed, errorMessage);
        view.showErrorMsg(errorMessage);
        view.enableForm();
    }

    @Override
    public void onUserCreateSuccess(User user) {
        Log.d(TAG, "onCommentSaveSuccess(), "+user);
        view.showInfoMsg("Пользователь создан");
        view.goUserEditPage(user);
    }

    @Override
    public void onUserCreateFail(String errorMessage) {
        Log.d(TAG, "onCreateFail(), "+errorMessage);
        view.hideProgressBar();
        view.showErrorMsg(errorMessage);
        view.enableForm();
    }


    // Внутренние методы
    private void createUser(String uid, String name) {

    }
}