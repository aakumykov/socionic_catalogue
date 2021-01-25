package ru.aakumykov.me.sociocat.reset_password_step1;

import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.z_base_view.iBaseView;

public interface iResetPasswordStep1 {

    enum ViewState {
        INITIAL,
        PROGRESS,
        SUCCESS,
        CHECKING_EMAIL,
        COMMON_ERROR,
        EMAIL_ERROR
    }

    interface View extends iBaseView {
        String getEmail();
        void finishWork();

        void disableForm();
        void enableForm();

        void showEmailThrobber();
        void hideEmailThrobber();

        void setState(ViewState state, int messageId);
        void setState(ViewState state, int messageId, @Nullable String messageDetails);

        void showEmailError(int messageId);
        void hideEmailError();
    }

    interface Presenter {

        void linkView(View view);
        void unlinkView();

        boolean isVirgin();
        void onFirstOpen();
        void onConfigChanged();

        void storeViewState(ViewState state, int messageId, String messageDetails);

        void onFormIsValid();
    }


    interface ResetPasswordCallbacks {
        void onEmailSendSucces();
        void onEmailSendFail(String errorMsg);
    }
}
