package ru.aakumykov.me.sociocat.user_change_password;

import androidx.annotation.Nullable;

import io.aakumykov.me.sociocat.R;
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
    private boolean isVirgin = true;


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
    public boolean isVirgin() {
        return isVirgin;
    }

    @Override
    public void onFirstOpen() {
        isVirgin = false;
        closeIfGuest();
    }

    @Override
    public void onConfigChanged() {
        closeIfGuest();
    }

    @Override
    public void onFormIsValid() {
        checkCurrentPassword();
    }

    @Override
    public void onCancelButtonClicked() {
        view.closePage();
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
    private void closeIfGuest() {
        if (!AuthSingleton.isLoggedIn()) {
            view.showToast(R.string.not_authorized);
            view.closePage();
        }
    }

    private void checkCurrentPassword() {

        view.setState(iUserChangePassword.ViewState.PROGRESS, R.string.checking_password);

        String email = usersSingleton.getCurrentUser().getEmail();

        AuthSingleton.checkPassword(email, view.getCurrentPassword(), new iAuthSingleton.CheckPasswordCallbacks() {
            @Override
            public void onUserCredentialsOk() {
                setNewPassword();
            }

            @Override
            public void onUserCredentialsNotOk(String errorMsg) {
                view.setState(iUserChangePassword.ViewState.ERROR, R.string.USER_CHANGE_PASSWORD_error_wrong_current_password, errorMsg);
            }
        });
    }

    private void setNewPassword() {

        view.setState(iUserChangePassword.ViewState.PROGRESS, R.string.USER_CHANGE_PASSWORD_saving_new_password);

        String newPassword = view.getNewPassword();

        AuthSingleton.changePassword(newPassword, new iAuthSingleton.ChangePasswordCallbacks() {
            @Override
            public void onChangePasswordSuccess() {
                view.setState(iUserChangePassword.ViewState.SUCCESS, R.string.USER_CHANGE_PASSWORD_password_successfully_changed);
            }

            @Override
            public void onChangePasswordError(String errorMsg) {
                view.setState(iUserChangePassword.ViewState.ERROR, R.string.USER_CHANGE_PASSWORD_error_changing_password);
            }
        });

    }

}
