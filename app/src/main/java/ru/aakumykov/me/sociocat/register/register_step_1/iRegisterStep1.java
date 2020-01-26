package ru.aakumykov.me.sociocat.register.register_step_1;


import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.interfaces.iBaseView;

public interface iRegisterStep1 {

    public enum ViewStatus {
        CHECKING,
        EMAIL_ERROR,
        COMMON_ERROR, SUCCESS
    }

    interface View extends iBaseView {
        String getEmail();

        void showEmailThrobber();
        void hideEmailThrobber();

        void disableForm();
        void enableForm();

        void showEmailError(String msgId);
        void hideEmailError();

        void showSuccessDialog();

        void accessDenied(int msgId);

        void setStatus(ViewStatus status, int errorMessageId);
        void setStatus(ViewStatus status, String errorMessage);
    }

    interface Presenter {
        void doInitialCheck();
        void produceRegistrationStep1();

        void linkView(View view);
        void unlinkView();
    }
}
