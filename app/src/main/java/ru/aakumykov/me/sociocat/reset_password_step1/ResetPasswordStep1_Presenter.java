package ru.aakumykov.me.sociocat.reset_password_step1;

import ru.aakumykov.me.sociocat.singletons.iAuthSingleton;
import ru.aakumykov.me.sociocat.singletons.AuthSingleton;


public class ResetPasswordStep1_Presenter implements iResetPasswordStep1.Presenter {

    private iResetPasswordStep1.View view;
    private boolean isVirgin = true;

    @Override
    public void linkView(iResetPasswordStep1.View view) {
        this.view = view;
    }

    @Override
    public void unlinkView() {
        this.view = new ResetPasswordStep1_ViewStub();
    }

    @Override
    public boolean isVirgin() {
        return isVirgin;
    }

    @Override
    public void onFirstOpen() {
        view.setState(iResetPasswordStep1.ViewState.INITIAL, -1);
    }

    @Override
    public void storeViewState(iResetPasswordStep1.ViewState state, int messageId, String messageDetails) {

    }

    @Override
    public void onConfigChanged() {

    }

    @Override
    public void resetPassword(final iResetPasswordStep1.ResetPasswordCallbacks callbacks) {

        String email = view.getEmail();

        AuthSingleton.resetPasswordEmail(email, new iAuthSingleton.ResetPasswordCallbacks() {
            @Override
            public void onEmailSendSuccess() {
                callbacks.onEmailSendSucces();
            }

            @Override
            public void onEmailSendFail(String errorMsg) {
                callbacks.onEmailSendFail(errorMsg);
            }
        });

    }
}
