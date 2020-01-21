package ru.aakumykov.me.sociocat.user_edit_email;

import android.content.Intent;

import androidx.annotation.Nullable;

import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.singletons.AuthSingleton;
import ru.aakumykov.me.sociocat.singletons.iAuthSingleton;
import ru.aakumykov.me.sociocat.user_edit_email.stubs.UserEmailEdit_ViewStub;
import ru.aakumykov.me.sociocat.utils.MyUtils;


class UserEditEmail_Presenter implements iUserEditEmail.iPresenter {

    private iUserEditEmail.iView view;

    private iAuthSingleton authSingleton = AuthSingleton.getInstance();


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

        String email = view.getEmail().trim();
        if (!MyUtils.isCorrectEmail(email)) {
            view.showEmailError(R.string.VALIDATION_mailformed_email);
            return;
        }

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (null == firebaseUser) {
            return;
        }

        firebaseUser.
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
