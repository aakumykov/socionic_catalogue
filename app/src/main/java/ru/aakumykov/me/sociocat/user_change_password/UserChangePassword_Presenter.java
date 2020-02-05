package ru.aakumykov.me.sociocat.user_change_password;

import android.content.Intent;

import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.singletons.AuthSingleton;
import ru.aakumykov.me.sociocat.singletons.UsersSingleton;
import ru.aakumykov.me.sociocat.singletons.iAuthSingleton;
import ru.aakumykov.me.sociocat.singletons.iUsersSingleton;
import ru.aakumykov.me.sociocat.user_change_password.stubs.UserChangePassword_ViewStub;


class UserChangePassword_Presenter implements iUserChangePassword.iPresenter {

    private iUserChangePassword.iView view;

    private iUserChangePassword.ViewState currentViewState;
    private int currentMessageId;
    private String currentMessageDetails;

    private iUsersSingleton usersSingleton = UsersSingleton.getInstance();


    @Override
    public void linkView(iUserChangePassword.iView view) {
        this.view = view;
    }

    @Override
    public void unlinkView() {
        this.view = new UserChangePassword_ViewStub();
    }

    @Override
    public void onUserLoggedOut() {
        view.closePage();
    }

    @Override
    public void onFirstOpen(@Nullable Intent intent) {
        if (!AuthSingleton.isLoggedIn()) {
            view.showToast(R.string.not_authorized);
            view.closePage();
            return;
        }
    }

    @Override
    public void onConfigChanged() {

    }

    @Override
    public void onFormIsValid() {
        checkCurrentPassword();
    }

    private void checkCurrentPassword() {

        view.setState(iUserChangePassword.ViewState.PROGRESS, R.string.checking_password);

        String email = usersSingleton.getCurrentUser().getEmail();

        AuthSingleton.checkPassword(email, view.getCurrentPassword(), new iAuthSingleton.CheckPasswordCallbacks() {
            @Override
            public void onUserCredentialsOk() {
                view.setState(iUserChangePassword.ViewState.SUCCESS, -1);
            }

            @Override
            public void onUserCredentialsNotOk(String errorMsg) {
                view.setState(iUserChangePassword.ViewState.ERROR, R.string.error_wrong_password, errorMsg);
            }
        });
    }

    @Override
    public void onCancelButtonClicked() {

    }

    @Override
    public void onBackPressed() {
        onCancelButtonClicked();
    }

    @Override
    public boolean onHomePressed() {
        onCancelButtonClicked();
        return true;
    }

    @Override
    public void storeViewState(iUserChangePassword.ViewState state, int messageId, @Nullable String messageDetails) {
        currentViewState = state;
        currentMessageId = messageId;
        currentMessageDetails = messageDetails;
    }


    // Внутренние методы
}
