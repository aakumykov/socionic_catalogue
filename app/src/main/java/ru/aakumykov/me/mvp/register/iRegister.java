package ru.aakumykov.me.mvp.register;

import ru.aakumykov.me.mvp.iBaseView;

public interface iRegister {

    interface RegistrationCallbacks {
        void onRegisrtationSuccess();
        void onRegisrtationFail(String errorMsg);
    }

    interface View extends iBaseView {
        String getName();
        String getEmail();
        String getPassword1();
        String getPassword2();

        void disableNameInput();
        void enableNameInput();

        void disableEmailInput();
        void enableEmailInput();

        void showNameError(int messageId);
        void showEmailError(int messageId);
        void showPassword1Error(int messageId);
        void showPassword2Error(int messageId);

        void disableForm();
        void enableForm();

        void finishRegistration(String email);
    }

    interface Presenter {
        void linkView(iRegister.View view);
        void unlinkView();

        void registerUser();
    }
}
