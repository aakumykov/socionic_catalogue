package io.gitlab.aakumykov.sociocat.register_step_1;


import io.gitlab.aakumykov.sociocat.z_base_view.iBaseView;

public interface iRegisterStep1 {

    enum ViewStates {
        INITIAL,
        CHECKING,
        PROGRESS,
        SUCCESS,
        EMAIL_ERROR,
        COMMON_ERROR
    }

    interface View extends iBaseView {
        String getEmail();

        void setState(ViewStates status, int messageId);
        void setState(ViewStates status, int messageId, String messageDetails);

        void showSuccessDialog();

        void accessDenied(int msgId);
    }

    interface Presenter {
        void doInitialCheck();
        void onRegisterButtonClicked();

        void linkView(View view);
        void unlinkView();

        boolean isVirgin();
        void storeViewStatus(ViewStates viewStatus, int messageId, String errorMessage);
        void onConfigChanged();
    }
}
