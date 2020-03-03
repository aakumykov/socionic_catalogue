package ru.aakumykov.me.sociocat.login;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.BaseView_Stub;

public class Login_View_Stub extends BaseView_Stub implements iLogin.View {
    @Override
    public void setState(iLogin.ViewState state, int messageId) {

    }

    @Override
    public void setState(iLogin.ViewState state, int messageId, @Nullable String messageDetails) {

    }

    @Override
    public void disableForm() {

    }

    @Override
    public void enableForm() {

    }

    @Override
    public void startLoginWithGoogle() {

    }

    @Override
    public void finishLogin(boolean isCancelled, Intent transitIntent) {

    }

    @Override
    public void notifyToConfirmEmail(String userId) {

    }

    @Override
    public String getEmail() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public void go2finishRegistration(@NonNull String userId) {

    }
}
