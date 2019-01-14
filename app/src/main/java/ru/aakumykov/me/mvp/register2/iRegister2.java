package ru.aakumykov.me.mvp.register2;

public interface iRegister2 {

    interface View {
        String getName();
        String getEmail();
        String getPassword1();
        String getPassword2();

        void showNameError();
        void showEmailError();
        void showPasswordError();

        void finishAndGoToApp();
    }

    interface Presenter {
        void linkView(iRegister2.View view);
        void unlinkView();

        void processRegistration();
    }
}
