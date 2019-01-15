package ru.aakumykov.me.mvp.register;

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
import ru.aakumykov.me.mvp.models.User;
import ru.aakumykov.me.mvp.services.AuthSingleton;
import ru.aakumykov.me.mvp.services.UsersSingleton;

public class Register_Presenter implements iRegister.Presenter {

    private iAuthSingleton authService = AuthSingleton.getInstance();
    private iUsersSingleton usersService = UsersSingleton.getInstance();
    private Map<String,Boolean> formCheckResults = new HashMap<>();
    private iRegister.View view;

    Register_Presenter() {}


    // Системные методы
    @Override
    public void linkView(iRegister.View view) {
        this.view = view;
    }

    @Override
    public void unlinkView() {
        this.view = null;
    }


    // Интерфейсные методы
    @Override
    public void registerUser() {
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
            setNameIsValid(false);
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
                setNameIsValid(false);
                view.showNameError(R.string.REGISTER2_name_already_used);
            }

            @Override
            public void onNotExists() {
                setNameIsValid(true);
                startRegister();
            }

            @Override
            public void onCheckFail(String errorMsg) {
                setNameIsValid(false);
                view.showErrorMsg(R.string.REGISTER2_error, errorMsg);
            }
        });
    }

    private void checkEmail() {
        String email = view.getEmail();

        if (TextUtils.isEmpty(email)) {
            setEmailIsValid(false);
            view.showEmailError(R.string.REGISTER2_cannot_be_empty);
            return;
        }

        Pattern pattern = Pattern.compile("^([a-z0-9+_]+[.-]?)*[a-z0-9]+@([a-z0-9]+[.-]?)*[a-z0-9]+\\.[a-z]+$");
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches()) {
            setEmailIsValid(false);
            view.showEmailError(R.string.REGISTER2_incorrect_email);
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
                setEmailIsValid(false);
                view.showEmailError(R.string.REGISTER2_email_already_used);
            }

            @Override
            public void onNotExists() {
                setEmailIsValid(true);
                startRegister();
            }

            @Override
            public void onCheckFail(String errorMsg) {
                setEmailIsValid(false);
                view.showErrorMsg(R.string.REGISTER2_error, errorMsg);
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

    private void setNameIsValid(boolean validity) {
        formCheckResults.put("name", validity);
    }

    private void setEmailIsValid(boolean validity) {
        formCheckResults.put("email", validity);
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


    private void startRegister() {
        if (formIsValid()) {
            registrationStep1();
        }
    }

    private void registrationStep1() {

        final String userName = view.getName();
        final String userEmail = view.getEmail();
        final String userPassword = view.getPassword1();

        try {
            authService.registerWithEmail(userEmail, userPassword, new iAuthSingleton.RegisterCallbacks() {

                @Override public void onRegSucsess(String userId, String email) {
                    // TODO: здесь нужен только userId
                    registrationStep2(userId, userName, userEmail);
                }

                @Override public void onRegFail(String errorMsg) {
                    view.enableForm();
                    view.showErrorMsg(R.string.REGISTER2_registration_error, errorMsg);
                }
            });
        } catch (Exception e) {
            view.enableForm();
            view.showErrorMsg(R.string.REGISTER2_registration_error, e.getMessage());
            e.printStackTrace();
        }

    }

    private void registrationStep2(String userId, String name, String email) {

        usersService.createUser(userId, name, email, new iUsersSingleton.CreateCallbacks() {
            @Override public void onUserCreateSuccess(User user) {
                authService.storeCurrentUser(user);
                view.finishAndGoToApp();
            }

            @Override public void onUserCreateFail(String errorMsg) {
                view.enableForm();
                view.showErrorMsg(R.string.REGISTER2_registration_error, errorMsg);
            }
        });
    }
}
