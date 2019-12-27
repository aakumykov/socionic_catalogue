package ru.aakumykov.me.sociocat.users.stubs;

import android.annotation.SuppressLint;

import ru.aakumykov.me.sociocat.models.User;
import ru.aakumykov.me.sociocat.users.iUsers;

@SuppressLint("Registered")
public class UserEdit_ViewStub extends Users_ViewStub implements iUsers.EditView {
    @Override
    public void displayUser(User user) {

    }

    @Override
    public <T> void displayAvatar(T avatar) {

    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getEmail() {
        return null;
    }

    @Override
    public String getAbout() {
        return null;
    }

    @Override
    public void showAvatarThrobber() {

    }

    @Override
    public void hideAvatarThrobber() {

    }

    @Override
    public void enableEditForm() {

    }

    @Override
    public void disableEditForm() {

    }

    @Override
    public void finishEdit(User user, boolean isSuccessfull) {

    }
}
