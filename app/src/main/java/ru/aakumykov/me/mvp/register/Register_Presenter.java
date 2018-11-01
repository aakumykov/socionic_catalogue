package ru.aakumykov.me.mvp.register;

import android.util.Log;

import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.interfaces.iAuthService;
import ru.aakumykov.me.mvp.interfaces.iCardsService;
import ru.aakumykov.me.mvp.models.User;

public class Register_Presenter implements
        iRegister.Presenter,
        iAuthService.RegisterCallbacks,
        iAuthService.CreateUserCallbacks
{
    private final static String TAG = "Register_Presenter";
    private iRegister.View view;
    private iCardsService model;
    private iAuthService authService;


    // Интерфейсные методы
    @Override
    public void regUserWithEmail(final String name, String email, String password) {
        Log.d(TAG, "regUserWithEmail()");

        authService.registerWithEmail(email, password, this);
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
        Log.d(TAG, "onCreateSuccess(), "+userId);
        view.hideProgressBar();
        view.showInfoMsg("userId: "+userId);

//        authService.createUser(userId, name, Register_Presenter.this);
    }

    @Override
    public void onRegFail(String errorMessage) {
        Log.d(TAG, "onCreateSuccess(), "+errorMessage);
        view.hideProgressBar();
        view.showErrorMsg(R.string.REGISTER_registration_failed, errorMessage);
    }

    @Override
    public void onCreateSuccess(User user) {
        Log.d(TAG, "onCreateSuccess(), "+user);
    }

    @Override
    public void onCreateFail(String errorMessage) {
        Log.d(TAG, "onCreateFail(), "+errorMessage);
    }
}
