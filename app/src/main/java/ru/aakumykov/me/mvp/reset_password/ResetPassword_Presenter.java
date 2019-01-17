package ru.aakumykov.me.mvp.reset_password;

import android.text.TextUtils;

import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.interfaces.iAuthSingleton;
import ru.aakumykov.me.mvp.services.AuthSingleton;


public class ResetPassword_Presenter implements iResetPassword.Presenter {

    private iResetPassword.View view;
    private iAuthSingleton authService = AuthSingleton.getInstance();

    @Override
    public void linkView(iResetPassword.View view) {
        this.view = view;
    }

    @Override
    public void unlinkView() {
        this.view = null;
    }

    @Override
    public void resetPassword(final iResetPassword.ResetPasswordCallbacks callbacks) {

        String email = view.getEmail();

        authService.resetPasswordEmail(email, new iAuthSingleton.ResetPasswordCallbacks() {
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
