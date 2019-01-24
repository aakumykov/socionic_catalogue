package ru.aakumykov.me.mvp.register;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.interfaces.iAuthSingleton;
import ru.aakumykov.me.mvp.interfaces.iUsersSingleton;
import ru.aakumykov.me.mvp.models.User;
import ru.aakumykov.me.mvp.services.AuthSingleton;
import ru.aakumykov.me.mvp.services.UsersSingleton;
import ru.aakumykov.me.mvp.utils.MySharedPreferences;
import ru.aakumykov.me.mvp.utils.MyUtils;

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
                registrationStep1();
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

        if (!MyUtils.isEmailCorrect(email)) {
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
                registrationStep1();
            }

            @Override
            public void onCheckFail(String errorMsg) {
                setEmailIsValid(false);
                view.showErrorMsg(R.string.REGISTER2_error_checking_form, errorMsg);
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

    private void registrationStep1() {
        if (formIsValid()) {
            registrationStep2();
        }
    }

    private void registrationStep2() {

        final String userName = view.getName();
        final String userEmail = view.getEmail();
        final String userPassword = view.getPassword1();

        try {
            authService.registerWithEmail(userEmail, userPassword, new iAuthSingleton.RegisterCallbacks() {

                @Override public void onRegSucsess(String userId, String email) {
                    // TODO: здесь нужен только userId
                    registrationStep3(userId, userName, userEmail);
                }

                @Override public void onRegFail(String errorMsg) {
                    view.enableForm();
                    view.showErrorMsg(R.string.REGISTER2_registration_error, errorMsg);
                }
            });
        } catch (Exception e) {
            onErrorOccured(e.getMessage());
            e.printStackTrace();
        }

    }

    private void registrationStep3(String userId, String name, final String email) {

        usersService.createUser(userId, name, email, new iUsersSingleton.CreateCallbacks() {

            @Override public void onUserCreateSuccess(User user) {
                registrationStep4(email);
            }

            @Override public void onUserCreateFail(String errorMsg) {
                onErrorOccured(errorMsg);
            }
        });
    }

    private void registrationStep4(final String email) {

        try {
            authService.sendSignInLinkToEmail(email, new iAuthSingleton.SendSignInLinkToEmailCallbacks() {
                @Override
                public void onSendSignInLinkToEmailSuccess() {
//                    MySharedPreferences mySharedPreferences =
//                            new MySharedPreferences(view.getAppContext(), Constants.SHARED_PREFERENCES_EMAIL);
//                    mySharedPreferences.store("email", email);

                    SharedPreferences sharedPreferences = view.getAppContext().getSharedPreferences(
                            Constants.SHARED_PREFERENCES_EMAIL,
                            Context.MODE_PRIVATE
                    );
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("email", email);
                    editor.apply();

                    authService.logout();
                    view.finishRegistration(email);
                }

                @Override
                public void onSendSignInLinkToEmailFail(String errorMsg) {
                    onErrorOccured(errorMsg);
                }
            });

        } catch (Exception e) {
            onErrorOccured(e.getMessage());
            e.printStackTrace();
        }

    }

    private void onErrorOccured(String errorMsg) {
        view.showErrorMsg(R.string.REGISTER2_registration_error, errorMsg);
        view.enableForm();
    }
}
