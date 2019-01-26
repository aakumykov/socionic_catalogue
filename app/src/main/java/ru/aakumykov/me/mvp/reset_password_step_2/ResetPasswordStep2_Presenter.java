package ru.aakumykov.me.mvp.reset_password_step_2;

import ru.aakumykov.me.mvp.interfaces.iAuthSingleton;
import ru.aakumykov.me.mvp.services.AuthSingleton;


public class ResetPasswordStep2_Presenter implements iResetPasswordStep2.Presenter {

    private iResetPasswordStep2.View view;
    private iAuthSingleton authService = AuthSingleton.getInstance();

    @Override
    public void linkView(iResetPasswordStep2.View view) {
        this.view = view;
    }

    @Override
    public void unlinkView() {
        this.view = null;
    }

    @Override
    public void setNewPassword(iResetPasswordStep2.SetNewPasswordCallbacks callbacks) {

    }
}
