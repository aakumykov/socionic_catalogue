package ru.aakumykov.me.sociocat.reset_password_step1;

import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.interfaces.iBaseView;

public interface iResetPasswordStep1 {

    enum ViewState {
        INITIAL,
        PROGRESS,
        SUCCESS,
        COMMON_ERROR,
        EMAIL_ERROR
    }

    interface View extends iBaseView {
        String getEmail();
        void finishWork();

        void disableForm();
        void enableForm();

        void setState(ViewState state, int messageId);
        void setState(ViewState state, int messageId, @Nullable String messageDetails);

        void showEmailError(int messageId);
        void hideEmailError();
    }

    interface Presenter {
        void resetPassword(ResetPasswordCallbacks callbacks);

        void linkView(View view);
        void unlinkView();

        boolean isVirgin();
        void onFirstOpen();
        void onConfigChanged();

        void storeViewState(ViewState state, int messageId, String messageDetails);
    }


    interface ResetPasswordCallbacks {
        void onEmailSendSucces();
        void onEmailSendFail(String errorMsg);
    }
}
