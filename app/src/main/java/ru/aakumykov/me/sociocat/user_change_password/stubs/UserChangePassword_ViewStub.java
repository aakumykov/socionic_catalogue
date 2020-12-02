package ru.aakumykov.me.sociocat.user_change_password.stubs;

import android.annotation.SuppressLint;

import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.user_change_password.iUserChangePassword;
import ru.aakumykov.me.sociocat.z_base_view.BaseView_Stub;

@SuppressLint("Registered")
public class UserChangePassword_ViewStub extends BaseView_Stub implements iUserChangePassword.iView {

    @Override
    public void setState(iUserChangePassword.ViewState state, int messageId) {

    }

    @Override
    public void setState(iUserChangePassword.ViewState state, int messageId, @Nullable String messageDetails) {

    }

    @Override
    public String getCurrentPassword() {
        return null;
    }

    @Override
    public String getNewPassword() {
        return null;
    }
}
