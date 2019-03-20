package ru.aakumykov.me.sociocat.reset_password_step1;

import ru.aakumykov.me.sociocat.interfaces.iBaseView;

interface iResetPasswordStep1 {

    interface View extends iBaseView {
        String getEmail();
        void finishWork();

        void disableForm();
        void enableForm();
    }

    interface Presenter {
        void resetPassword(ResetPasswordCallbacks callbacks);

        void linkView(View view);
        void unlinkView();
    }


    interface ResetPasswordCallbacks {
        void onEmailSendSucces();
        void onEmailSendFail(String errorMsg);
    }
}