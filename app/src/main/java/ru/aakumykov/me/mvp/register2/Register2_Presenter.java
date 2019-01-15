package ru.aakumykov.me.mvp.register2;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.interfaces.iAuthSingleton;
import ru.aakumykov.me.mvp.interfaces.iUsersSingleton;
import ru.aakumykov.me.mvp.services.AuthSingleton;
import ru.aakumykov.me.mvp.services.UsersSingleton;

public class Register2_Presenter implements iRegister2.Presenter {

    private iAuthSingleton authService = AuthSingleton.getInstance();
    private iUsersSingleton usersService = UsersSingleton.getInstance();
    private Map<String,Boolean> formCheckResults = new HashMap<>();
    private iRegister2.View view;

    Register2_Presenter() {}


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

        if (formIsValid()) {
            doRegistration(callbacks);
        }
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
            formCheckResults.put("email", false);
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
                formCheckResults.put("name", false);
            }

            @Override
            public void onNotExists() {
                formCheckResults.put("name", true);
            }

            @Override
            public void onCheckFail(String errorMsg) {
                view.showErrorMsg(R.string.REGISTER2_error, errorMsg);
                formCheckResults.put("name", false);
            }
        });
    }

    private void checkEmail() {
        String email = view.getEmail();

        if (TextUtils.isEmpty(email)) {
            view.showEmailError(R.string.REGISTER2_cannot_be_empty);
            formCheckResults.put("email", false);
            return;
        }

        Pattern pattern = Pattern.compile("^([a-z0-9+_]+[.-]?)*[a-z0-9]+@([a-z0-9]+[.-]?)*[a-z0-9]+\\.[a-z]+$");
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches()) {
            view.showEmailError(R.string.REGISTER2_incorrect_email);
            formCheckResults.put("email", false);
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
                formCheckResults.put("email", false);
            }

            @Override
            public void onNotExists() {
                formCheckResults.put("email", true);
            }

            @Override
            public void onCheckFail(String errorMsg) {
                view.showErrorMsg(R.string.REGISTER2_error, errorMsg);
                formCheckResults.put("email", false);
            }
        });
    }

    private void checkPassword() {
        String password1 = view.getPassword1();
        String password2 = view.getPassword2();

        if (TextUtils.isEmpty(password1)) {
            view.showPassword1Error(R.string.REGISTER2_cannot_be_empty);
            formCheckResults.put("password", false);
            return;
        }

        if (TextUtils.isEmpty(password2)) {
            view.showPassword2Error(R.string.REGISTER2_cannot_be_empty);
            formCheckResults.put("password", false);
            return;
        }

        if (!password1.equals(password2)) {
            view.showPassword1Error(R.string.REGISTER2_passwords_mismatch);
            view.showPassword2Error(R.string.REGISTER2_passwords_mismatch);
            formCheckResults.put("password", false);
            return;
        }

        if (password1.length() < Constants.PASSWORD_MIN_LENGTH) {
            view.showPassword1Error(R.string.REGISTER2_password_too_short);
            formCheckResults.put("password", false);
            return;
        }

        formCheckResults.put("password", true);
    }

    private boolean formIsValid() {
        Set<Map.Entry<String,Boolean>> entrySet = formCheckResults.entrySet();
        int formSize = entrySet.size();
        int resultsSize = 0;
        for (Map.Entry entry : entrySet) {
            boolean value = (Boolean) entry.getValue();
            if (value) resultsSize += 1;
            else resultsSize -= 1;
        }
        return (resultsSize == formSize);
    }

    private void doRegistration(iRegister2.RegistrationCallbacks callbacks) {
        view.showToast("РЕГИСТРАЦИЯ ПОШЛА");
    }
}
