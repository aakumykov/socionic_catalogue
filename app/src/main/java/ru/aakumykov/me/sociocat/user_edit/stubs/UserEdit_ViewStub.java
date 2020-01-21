package ru.aakumykov.me.sociocat.user_edit.stubs;

import android.annotation.SuppressLint;

import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.BaseView_Stub;
import ru.aakumykov.me.sociocat.models.User;
import ru.aakumykov.me.sociocat.user_edit.iUserEdit;

@SuppressLint("Registered")
public class UserEdit_ViewStub extends BaseView_Stub implements iUserEdit.iView {
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
    public void pickImage() {

    }

    @Override
    public void showAvatarRemoveDialog() {

    }

    @Override
    public void showCancelEditionDialog() {

    }

    @Override
    public void validateForm() {

    }

    @Override
    public void finishEdition(@Nullable User user) {

    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public void showPasswordError(String errorMsg) {

    }

    @Override
    public <T> void displayAvatar(T avatar) {

    }

    @Override
    public void removeAvatar() {

    }

    @Override
    public void showAvatarThrobber() {

    }

    @Override
    public void hideAvatarThrobber() {

    }

    @Override
    public void showAvatarError() {

    }

    @Override
    public void hideAvatarError() {

    }

    @Override
    public <T> void fillEditForm(User user, T avatar) {

    }

    @Override
    public void disableEditForm() {

    }

    @Override
    public void enableEditForm() {

    }
}
