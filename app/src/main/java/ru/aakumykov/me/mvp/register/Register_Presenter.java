package ru.aakumykov.me.mvp.register;

import android.util.Log;

import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.interfaces.iAuthSingleton;
import ru.aakumykov.me.mvp.interfaces.iCardsSingleton;
import ru.aakumykov.me.mvp.models.User;

// TODO: проверять имя пользователя

public class Register_Presenter implements
        iRegister.Presenter,
        iAuthSingleton.RegisterCallbacks,
        iAuthSingleton.CreateUserCallbacks
{
    private final static String TAG = "Register_Presenter";
    private iRegister.View view;
    // TODO: Модель-то бывает разная
    private iCardsSingleton model;
    private iAuthSingleton authService;
    private User userDraft;

    Register_Presenter() {
//        iAuthStateListener authStateListener = new AuthStateListener();
    }

    // Интерфейсные методы
    @Override
    public void regUserWithEmail(final String name, String email, String password) {
        Log.d(TAG, "regUserWithEmail()");

        userDraft = new User(name, email, null);

        try {
            authService.registerWithEmail(email, password, this);
        }
        catch (Exception e) {
            view.hideProgressBar();
            view.enableForm();
            view.showErrorMsg(R.string.REGISTER_registration_failed, e.getMessage());
            e.printStackTrace();
        }

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
    public void linkCardsService(iCardsSingleton model) {
        this.model = model;
    }
    @Override
    public void unlinkCardsService() {
        this.model = null;
    }

    @Override
    public void linkAuthService(iAuthSingleton authService) {
        this.authService = authService;
    }
    @Override
    public void unlinkAuthService() {
        this.authService = null;
    }


    // Коллбеки
    @Override
    public void onRegSucsess(String userId) {
        Log.d(TAG, "onRegSucsess(), "+userId);
        view.hideProgressBar();
        view.showInfoMsg("userId: "+userId);

        try {
            authService.createUser(userId, userDraft, this);
        }
        catch (Exception e) {
            view.hideProgressBar();
            view.enableForm();
            view.showErrorMsg(e.getMessage());
            e.printStackTrace();
        }
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
        Log.d(TAG, "onCommentCreateSuccess(), "+user);
        view.showInfoMsg("Пользователь создан");
        view.goUserPage(user);
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
