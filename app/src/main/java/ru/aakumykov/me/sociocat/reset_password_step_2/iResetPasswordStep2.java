package ru.aakumykov.me.sociocat.reset_password_step_2;

import ru.aakumykov.me.sociocat.base_view.iBaseView;

interface iResetPasswordStep2 {

    interface View extends iBaseView {
        String getPassword1();
        String getPassword2();

        void disableForm();
        void enableForm();

        void finishWork();
    }

    interface Presenter {
        void setNewPassword(SetNewPasswordCallbacks callbacks);

        void linkView(View view);
        void unlinkView();
    }


    interface SetNewPasswordCallbacks {
        void onNewPasswordSetSucces();
        void onNewPasswordSetFail(String errorMsg);
    }
}
