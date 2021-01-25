package ru.aakumykov.me.sociocat.reset_password_step_2;

import ru.aakumykov.me.sociocat.singletons.AuthSingleton;
import ru.aakumykov.me.sociocat.singletons.iAuthSingleton;


public class ResetPasswordStep2_Presenter implements iResetPasswordStep2.Presenter {

    private iResetPasswordStep2.View view;
    private iAuthSingleton authSingleton = AuthSingleton.getInstance();

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
