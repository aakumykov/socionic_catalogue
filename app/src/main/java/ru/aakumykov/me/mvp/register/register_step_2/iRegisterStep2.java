package ru.aakumykov.me.mvp.register.register_step_2;


import android.content.Intent;

import ru.aakumykov.me.mvp.iBaseView;

public interface iRegisterStep2 {

    interface View extends iBaseView {
        void hideUserMessage();

        String getUserName();
        String getPassword1();
        String getPassword2();

        void showUserNameError(int msgId);
        void showPassword1Error(int msgId);
        void showPassword2Error(int msgId);

        void disableForm();
        void enableForm();

        void showNameThrobber();
        void hideNameThrobber();

        void hideNameError();

        void goMainPage();
    }

    interface Presenter {
        void processRegistration(Intent intent);
        void linkView(View view);
        void unlinkView();
    }
}
