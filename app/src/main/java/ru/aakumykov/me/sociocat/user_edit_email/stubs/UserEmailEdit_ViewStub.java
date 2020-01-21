package ru.aakumykov.me.sociocat.user_edit_email.stubs;

import android.annotation.SuppressLint;

import ru.aakumykov.me.sociocat.BaseView_Stub;
import ru.aakumykov.me.sociocat.models.User;
import ru.aakumykov.me.sociocat.user_edit_email.iUserEditEmail;

@SuppressLint("Registered")
public class UserEmailEdit_ViewStub extends BaseView_Stub implements iUserEditEmail.iView {

    @Override
    public void displayCurrentEmail(User user) {

    }

    @Override
    public String getEmail() {
        return null;
    }

    @Override
    public void showEmailError(int errorMsgId) {

    }
}
