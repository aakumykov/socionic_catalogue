package io.gitlab.aakumykov.sociocat.user_edit_email;

import androidx.annotation.Nullable;

import io.gitlab.aakumykov.sociocat.z_base_view.iBaseView;

public interface iUserEditEmail {

    enum ViewState {
        SUCCESS,
        PROGRESS,
        EMAIL_ERROR,
        ERROR
    }

    interface iView extends iBaseView {
        void setViewState(ViewState state, int messageId);
        void setViewState(ViewState state, int messageId, @Nullable String errorDetails);

        void displayCurrentEmail(String email);

        String getEmail();

        void validateForm();

        void showEmailError(int errorMsgId);
        void showPasswordError(int errorMsgId);

        void disableForm();
        void enableForm();

        String getPassword();
    }

    interface iPresenter {
        void linkView(iView view);
        void unlinkView();

        boolean isVirgin();
        void onFirstOpen();
        void onConfigChanged();

        void onFormIsValid();

        void onSaveButtonClicked();
        void onCancelButtonClicked();

        void onBackPressed();
        boolean onHomePressed();

        void storeViewState(ViewState state, int messageId, @Nullable String errorDetails);
    }

}
