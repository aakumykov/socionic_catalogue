package ru.aakumykov.me.sociocat.reset_password_step1;

import android.annotation.SuppressLint;

import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.BaseView;

@SuppressLint("Registered")
public class ResetPasswordStep1_ViewStub extends BaseView implements iResetPasswordStep1.View {

    @Override
    public void onUserLogin() {

    }

    @Override
    public void onUserLogout() {

    }

    @Override
    public String getEmail() {
        return null;
    }

    @Override
    public void finishWork() {

    }

    @Override
    public void disableForm() {

    }

    @Override
    public void enableForm() {

    }

    @Override
    public void setState(iResetPasswordStep1.ViewState state, int messageId) {

    }

    @Override
    public void setState(iResetPasswordStep1.ViewState state, int messageId, @Nullable String messageDetails) {

    }

    @Override
    public void showEmailError(int messageId) {

    }

    @Override
    public void hideEmailError() {

    }
}
