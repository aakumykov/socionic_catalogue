package ru.aakumykov.me.mvp.register1;


import ru.aakumykov.me.mvp.iBaseView;

public interface iRegister1 {

    interface View extends iBaseView {
        String getEmail();
        void disableForm();
        void enableForm();
        void showEmailError(String msgId);
        void showSuccessDialog();
    }

    interface Presenter {
//        void processInputIntent(@Nullable Intent intent);
        void sendRegistrationEmail();

        void linkView(View view);
        void unlinkView();
    }
}
