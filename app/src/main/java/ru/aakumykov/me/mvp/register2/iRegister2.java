package ru.aakumykov.me.mvp.register2;

public interface iRegister2 {

    interface RegistrationCallbacks {
        void onRegisrtationSuccess();
        void onRegisrtationFail(String errorMsg);
    }

    interface View {
        String getName();
        String getEmail();
        String getPassword1();
        String getPassword2();

        void showNameError(int messageId);
        void showEmailError(int messageId);
        void showPasswordError(int messageId);

        void disableForm();
        void enableForm();

        void finishAndGoToApp();
    }

    interface Presenter {
        void linkView(iRegister2.View view);
        void unlinkView();

        void registerUser(RegistrationCallbacks callbacks);
    }
}
