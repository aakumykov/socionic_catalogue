package ru.aakumykov.me.sociocat.register.register_step_1;


import ru.aakumykov.me.sociocat.interfaces.iBaseView;

public interface iRegisterStep1 {

    interface View extends iBaseView {
        String getEmail();

        void showEmailChecked();
        void hideEmailChecked();

        void disableForm();
        void enableForm();

        void showEmailError(int msgId);

        void showSuccessDialog();

        void accessDenied(int msgId);
    }

    interface Presenter {
        void doInitialCheck();
        void produceRegistrationStep1();

        void linkView(View view);
        void unlinkView();
    }
}