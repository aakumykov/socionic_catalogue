package ru.aakumykov.me.mvp.register_confirmation;

import android.content.Intent;
import android.support.annotation.Nullable;

import ru.aakumykov.me.mvp.iBaseView;

interface iRegisterConfirmation {

    interface View extends iBaseView {
        void showNeedsConfirmationMessage();
        void showConfirmationSuccessMessage();
        void showConfirmationErrorMessage();
        void showEmailConfirmNotification();
        void goMainPage();
        void showOkButton();
        void showLeaveButton();
        void hideLeaveButton();
        void hideSendButton();
    }

    interface Presenter {
        void processInputIntent(@Nullable Intent intent);
        void sendEmailConfirmation();

        void linkView(View view);
        void unlinkView();
    }
}
