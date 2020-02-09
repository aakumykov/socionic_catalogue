package ru.aakumykov.me.sociocat.register_step_2;


import android.content.Intent;

import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.interfaces.iBaseView;

public interface iRegisterStep2 {

    enum ViewState {
        INITIAL,
        PROGRESS,
        SUCCESS,
        CHECKING_USER_NAME,
        NAME_ERROR,
        ERROR
    }

    interface View extends iBaseView {
        void hideUserMessage();

        void setState(ViewState state, int messageId);
        void setState(ViewState state, int messageId, @Nullable String messageDetails);

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

        void goMainPage();
    }

    interface Presenter {
        void processRegistration(Intent intent);
        void linkView(View view);
        void unlinkView();

        void storeViewState(ViewState viewState, int messageId, String messageDetails);

        boolean isVirgin();
        void processInputIntent(@Nullable Intent intent);
        void onConfigChanged();

        void onFormIsValid();

        void onBackPressed();
        void onHomePressed();
    }
}
