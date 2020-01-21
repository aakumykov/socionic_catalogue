package ru.aakumykov.me.sociocat.user_edit_email;

import android.content.Intent;

import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.user_edit_email.stubs.UserEmailEdit_ViewStub;


class UserEditEmail_Presenter implements iUserEditEmail.iPresenter {

    private iUserEditEmail.iView view;


    @Override
    public void linkView(iUserEditEmail.iView view) {
        this.view = view;
    }

    @Override
    public void unlinkView() {
        this.view = new UserEmailEdit_ViewStub();
    }

    @Override
    public void onFirstOpen(@Nullable Intent intent) {
//        view.displayCurrentEmail(currentItem);
    }

    @Override
    public void onConfigChanged() {
//        view.displayCurrentEmail(currentItem);
    }

    @Override
    public void onSaveButtonClicked() {
        view.showToast(R.string.PAGE_TEMPLATE_button_clicked);
    }

    @Override
    public void onCancelButtonClicked() {

    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public boolean onHomePressed() {

        return true;
    }
}
