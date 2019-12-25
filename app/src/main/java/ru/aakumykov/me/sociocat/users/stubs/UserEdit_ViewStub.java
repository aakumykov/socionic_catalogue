package ru.aakumykov.me.sociocat.users.stubs;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;

import ru.aakumykov.me.sociocat.BaseView;
import ru.aakumykov.me.sociocat.models.User;
import ru.aakumykov.me.sociocat.users.iUsers;

@SuppressLint("Registered")
public class UserEdit_ViewStub extends Users_ViewStub implements iUsers.EditView {
    @Override
    public void fillUserForm(User user) {

    }

    @Override
    public void displayAvatar(String imageURL, boolean justSelected) {

    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getAbout() {
        return null;
    }

    @Override
    public Bitmap getImageBitmap() {
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
