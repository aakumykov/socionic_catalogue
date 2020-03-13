package ru.aakumykov.me.sociocat.register_step_1;

import android.annotation.SuppressLint;

import ru.aakumykov.me.sociocat.base_view.BaseView_Stub;

@SuppressLint("Registered")
public class RegisterStep1_ViewStub extends BaseView_Stub implements iRegisterStep1.View {


    @Override
    public String getEmail() {
        return null;
    }

    @Override
    public void setState(iRegisterStep1.ViewStates status, int messageId) {

    }

    @Override
    public void setState(iRegisterStep1.ViewStates status, int messageId, String messageDetails) {

    }

    @Override
    public void showSuccessDialog() {

    }

    @Override
    public void accessDenied(int msgId) {

    }
}
