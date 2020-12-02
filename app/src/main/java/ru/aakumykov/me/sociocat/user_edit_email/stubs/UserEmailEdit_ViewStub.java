package ru.aakumykov.me.sociocat.user_edit_email.stubs;

import android.annotation.SuppressLint;

import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.user_edit_email.iUserEditEmail;
import ru.aakumykov.me.sociocat.z_base_view.BaseView_Stub;

@SuppressLint("Registered")
public class UserEmailEdit_ViewStub extends BaseView_Stub implements iUserEditEmail.iView {

    @Override
    public void setViewState(iUserEditEmail.ViewState state, int messageId) {

    }

    @Override
    public void setViewState(iUserEditEmail.ViewState state, int messageId, @Nullable String errorDetails) {

    }

    @Override
    public void displayCurrentEmail(String email) {

    }

    @Override
    public String getEmail() {
        return null;
    }

    @Override
    public void validateForm() {

    }

    @Override
    public void showEmailError(int errorMsgId) {

    }

    @Override
    public void showPasswordError(int errorMsgId) {

    }

    @Override
    public void disableForm() {

    }

    @Override
    public void enableForm() {

    }

    @Override
    public String getPassword() {
        return null;
    }
}
