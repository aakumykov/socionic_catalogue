package ru.aakumykov.me.mvp.register;

import android.util.Log;

import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.interfaces.iAuthService;
import ru.aakumykov.me.mvp.interfaces.iCardsService;
import ru.aakumykov.me.mvp.models.User;

// TODO: проверять имя пользователя

public class Register_Presenter implements
        iRegister.Presenter,
        iAuthService.RegisterCallbacks,
        iAuthService.CreateUserCallbacks
{
    private final static String TAG = "Register_Presenter";
    private iRegister.View view;
    // TODO: Модель-то бывает разная
    private iCardsService model;
    private iAuthService authService;

    private User userDraft;

    // Интерфейсные методы
    @Override
    public void regUserWithEmail(final String name, String email, String password) {
        Log.d(TAG, "regUserWithEmail()");

        userDraft = new User(name, email, null);

        authService.registerWithEmail(email, password, this);

        // Для проверки
//        authService.createUser("58lQdvxNDlSDE7iot0yqtxrNOg53", userDraft, this);
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


    // Коллбеки
    @Override
    public void onRegSucsess(String userId) {
        Log.d(TAG, "onRegSucsess(), "+userId);
        view.hideProgressBar();
        view.showInfoMsg("userId: "+userId);

        // try
        authService.createUser(userId, userDraft, this);
    }

    @Override
    public void onRegFail(String errorMessage) {
        Log.d(TAG, "onRegFail(), "+errorMessage);
        view.hideProgressBar();
        view.showErrorMsg(errorMessage);
        view.enableForm();
    }


    @Override
    public void onCreateSuccess(User user) {
        Log.d(TAG, "onCreateSuccess(), "+user);
        view.showInfoMsg("Пользователь создан");
    }

    @Override
    public void onCreateFail(String errorMessage) {
        Log.d(TAG, "onCreateFail(), "+errorMessage);
        view.hideProgressBar();
        view.showErrorMsg(errorMessage);
        view.enableForm();
    }


    // Внутренние методы
    private void createUser(String uid, String name) {

    }
}
