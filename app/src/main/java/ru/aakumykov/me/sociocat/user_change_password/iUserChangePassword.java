package ru.aakumykov.me.sociocat.user_change_password;

import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.base_view.iBaseView;

public interface iUserChangePassword {

    enum ViewState {
        PROGRESS,
        SUCCESS,
        ERROR/*,
        CURRENT_PASSWORD_ERROR,
        NEW_PASSWORD_ERROR*/
    }

    interface iView extends iBaseView {
        void setState(ViewState state, int messageId);
        void setState(ViewState state, int messageId, @Nullable String messageDetails);

        String getCurrentPassword();
        String getNewPassword();
    }

    interface iPresenter {
        void linkView(iView view);
        void unlinkView();

        void onUserLoggedOut();

        boolean isVirgin();
        void onFirstOpen();
        void onConfigChanged();

        void onFormIsValid();

        void onCancelButtonClicked();
        void onBackPressed();
        boolean onHomePressed();

        void storeViewState(ViewState state, int messageId, @Nullable String messageDetails);

    }

}
