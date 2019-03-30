package ru.aakumykov.me.sociocat.reset_password_step1;

import ru.aakumykov.me.sociocat.interfaces.iAuthSingleton;
import ru.aakumykov.me.sociocat.singletons.AuthSingleton;


public class ResetPasswordStep1_Presenter implements iResetPasswordStep1.Presenter {

    private iResetPasswordStep1.View view;
    private iAuthSingleton authSingleton = AuthSingleton.getInstance();

    @Override
    public void linkView(iResetPasswordStep1.View view) {
        this.view = view;
    }

    @Override
    public void unlinkView() {
        this.view = null;
    }

    @Override
    public void resetPassword(final iResetPasswordStep1.ResetPasswordCallbacks callbacks) {

        String email = view.getEmail();

        authSingleton.resetPasswordEmail(email, new iAuthSingleton.ResetPasswordCallbacks() {
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
