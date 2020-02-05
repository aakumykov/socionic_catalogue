package ru.aakumykov.me.sociocat.user_change_password;

import android.content.Intent;

import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.user_change_password.models.Item;
import ru.aakumykov.me.sociocat.interfaces.iBaseView;

public interface iUserChangePassword {

    enum ViewState {
        PROGRESS,
        SUCCESS,
        ERROR
    }

    interface iView extends iBaseView {
        void setState(ViewState state, int messageId);
        void setState(ViewState state, int messageId, @Nullable String messageDetails);

        String getCurrentPassword();
    }

    interface iPresenter {
        void linkView(iView view);
        void unlinkView();

        void onUserLoggedOut();

        void onFirstOpen(@Nullable Intent intent);
        void onConfigChanged();

        void onFormIsValid();

        void onCancelButtonClicked();
        void onBackPressed();
        boolean onHomePressed();

        void storeViewState(ViewState state, int messageId, @Nullable String messageDetails);
    }

}
