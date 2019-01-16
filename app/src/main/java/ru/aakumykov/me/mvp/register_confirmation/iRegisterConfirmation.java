package ru.aakumykov.me.mvp.register_confirmation;

import android.content.Intent;
import android.support.annotation.Nullable;

import ru.aakumykov.me.mvp.iBaseView;

interface iRegisterConfirmation {

    interface View extends iBaseView {
        void showEmailNeedsConfirmation(String email);

        void showConfirmationSuccess();
        void showConfirmationError();

        void notifyEmailNeedsConfirmation();
         void showEmailSending();
         void showEmailSendSuccess();
         void showEmailSendError();
    }

    interface Presenter {
        void processInputIntent(@Nullable Intent intent);
        void sendEmailConfirmation();

        void linkView(View view);
        void unlinkView();
    }
}
