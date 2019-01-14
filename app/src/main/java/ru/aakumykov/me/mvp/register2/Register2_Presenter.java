package ru.aakumykov.me.mvp.register2;

import ru.aakumykov.me.mvp.interfaces.iAuthSingleton;
import ru.aakumykov.me.mvp.services.AuthSingleton;

public class Register2_Presenter implements iRegister2.Presenter {

    private iAuthSingleton authService;
    private iRegister2.View view;
    private boolean formIsValid = false;

    public Register2_Presenter() {
        authService = AuthSingleton.getInstance();
    }


    // Системные методы
    @Override
    public void linkView(iRegister2.View view) {
        this.view = view;
    }

    @Override
    public void unlinkView() {
        this.view = null;
    }


    // Интерфейсные методы
    @Override
    public void registerUser(iRegister2.RegistrationCallbacks callbacks) {

//        authService.registerWithEmail();
    }

    // Внутренние методы
    private void checkFields() {
        checkName();
        checkEmail();
        checkPassword();
    }

    private void checkName(){
        String name = view.getName();

    }

    private void checkEmail() {
        String email = view.getEmail();

    }

    private void checkPassword() {
        String password1 = view.getPassword1();
        String password2 = view.getPassword2();
    }
}
