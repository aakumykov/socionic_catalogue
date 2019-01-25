package ru.aakumykov.me.mvp.register2;


import android.content.Intent;

import ru.aakumykov.me.mvp.iBaseView;

public interface iRegister2 {

    interface View extends iBaseView {
        void hideUserMessage();

        String getPassword1();
        String getPassword2();

        void showPassword1Error(int msgId);
        void showPassword2Error(int msgId);

        void disableForm();
        void enableForm();

        void goMainPage();
    }

    interface Presenter {
        void finishRegistration(Intent intent);
        void linkView(View view);
        void unlinkView();
    }
}
