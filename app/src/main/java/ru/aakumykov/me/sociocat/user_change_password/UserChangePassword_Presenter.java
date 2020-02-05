package ru.aakumykov.me.sociocat.user_change_password;

import android.content.Intent;

import androidx.annotation.Nullable;

import java.util.Date;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.user_change_password.models.Item;
import ru.aakumykov.me.sociocat.user_change_password.stubs.UserChangePassword_ViewStub;


class UserChangePassword_Presenter implements iUserChangePassword.iPresenter {

    private iUserChangePassword.iView view;
    private Item currentItem;


    @Override
    public void linkView(iUserChangePassword.iView view) {
        this.view = view;
    }

    @Override
    public void unlinkView() {
        this.view = new UserChangePassword_ViewStub();
    }

    @Override
    public boolean hasItem() {
        return null != currentItem;
    }

    @Override
    public void onFirstOpen(@Nullable Intent intent) {

    }

    @Override
    public void onConfigChanged() {

    }

    @Override
    public void onRefreshRequested() {

    }

    @Override
    public void onButtonClicked() {
        view.showToast(R.string.PAGE_TEMPLATE_button_clicked);
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public boolean onHomePressed() {
        return true;
    }


    // Внутренние методы
}
