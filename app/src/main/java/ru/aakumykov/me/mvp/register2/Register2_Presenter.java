package ru.aakumykov.me.mvp.register2;

import android.text.TextUtils;

import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.interfaces.iAuthSingleton;
import ru.aakumykov.me.mvp.interfaces.iUsersSingleton;
import ru.aakumykov.me.mvp.services.AuthSingleton;
import ru.aakumykov.me.mvp.services.UsersSingleton;

public class Register2_Presenter implements iRegister2.Presenter {

    private iAuthSingleton authService;
    private iUsersSingleton usersService;
    private iRegister2.View view;
    private boolean formIsValid = false;

    Register2_Presenter() {
        authService = AuthSingleton.getInstance();
        usersService = UsersSingleton.getInstance();
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
        checkFields();

    }

    // Внутренние методы
    private void checkFields() {
        checkName();
        checkEmail();
        checkPassword();
    }

    private void checkName(){
        String name = view.getName();

        if (TextUtils.isEmpty(name)) {
            view.showNameError(R.string.REGISTER2_cannot_be_empty);
            return;
        }

        view.disableNameInput();

        usersService.checkNameExists(name, new iUsersSingleton.CheckExistanceCallbacks() {
            @Override
            public void onCheckComplete() {
                view.enableNameInput();
            }

            @Override
            public void onExists() {
                view.showNameError(R.string.REGISTER2_name_already_used);
            }

            @Override
            public void onNotExists() {

            }

            @Override
            public void onCheckFail(String errorMsg) {
                view.showErrorMsg(R.string.REGISTER2_error, errorMsg);
            }
        });
    }

    private void checkEmail() {
        String email = view.getEmail();

        if (TextUtils.isEmpty(email)) {
            view.showEmailError(R.string.REGISTER2_cannot_be_empty);
            return;
        }

        view.disableEmailInput();

        usersService.checkEmailExists(email, new iUsersSingleton.CheckExistanceCallbacks() {
            @Override
            public void onCheckComplete() {
                view.enableEmailInput();
            }

            @Override
            public void onExists() {
                view.showEmailError(R.string.REGISTER2_email_already_used);
            }

            @Override
            public void onNotExists() {

            }

            @Override
            public void onCheckFail(String errorMsg) {
                view.showErrorMsg(R.string.REGISTER2_error, errorMsg);
            }
        });
    }

    private void checkPassword() {
        String password1 = view.getPassword1();
        String password2 = view.getPassword2();
    }
}
