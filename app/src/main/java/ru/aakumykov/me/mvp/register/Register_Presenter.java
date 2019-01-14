package ru.aakumykov.me.mvp.register;

import android.text.TextUtils;
import android.util.Log;

import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.interfaces.iAuthSingleton;
import ru.aakumykov.me.mvp.interfaces.iUsersSingleton;
import ru.aakumykov.me.mvp.models.User;
import ru.aakumykov.me.mvp.services.AuthSingleton;
import ru.aakumykov.me.mvp.services.UsersSingleton;
import ru.aakumykov.me.mvp.utils.Translator;

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
    public void regUserWithEmail() {
        String email = view.getEmail();
        String password = view.getPassword();
        String passwordConfirmation = view.getPasswordConfirmation();

        if (TextUtils.isEmpty(email)) {
            view.showErrorMsg(R.string.REGISTER_enter_email);
            view.focusEmail();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            view.showErrorMsg(R.string.REGISTER_enter_password);
            view.focusPassword();
            return;
        }

        if (TextUtils.isEmpty(passwordConfirmation)) {
            view.showErrorMsg(R.string.REGISTER_enter_password_confirmation);
            view.focusPasswordConfigmation();
            return;
        }

        // Случай двух пустых паролей обрабатывается выше
        if (!password.equals(passwordConfirmation)) {
            view.showErrorMsg(R.string.REGISTER_passwords_mismatch);
            view.focusPasswordConfigmation();
            return;
        }

        try {
            view.showProgressBar();
            view.showInfoMsg(R.string.REGISTER2_registering_user);

            authService.registerWithEmail(email, password, this);
        }
        catch (Exception e) {
            view.hideProgressBar();
            view.enableForm();
            view.showErrorMsg(Translator.translate(e.getMessage()));
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
        view.showInfoMsg(R.string.REGISTER_creating_user);
        usersService.createUser(userId, email, this);
    }

    @Override
    public void onRegFail(String errorMessage) {
        Log.d(TAG, "onRegFail(), "+errorMessage);
        view.hideProgressBar();
        view.showErrorMsg(Translator.translate(errorMessage));
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
        view.showErrorMsg(R.string.REGISTER_error_creating_user, errorMessage);
        view.enableForm();
    }


    // Внутренние методы
    private void createUser(String uid, String name) {

    }
}
