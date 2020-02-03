package ru.aakumykov.me.sociocat.user_edit_email;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.models.User;
import ru.aakumykov.me.sociocat.singletons.AuthSingleton;
import ru.aakumykov.me.sociocat.singletons.UsersSingleton;
import ru.aakumykov.me.sociocat.singletons.iAuthSingleton;
import ru.aakumykov.me.sociocat.singletons.iUsersSingleton;
import ru.aakumykov.me.sociocat.user_edit_email.stubs.UserEmailEdit_ViewStub;

import static android.app.Activity.RESULT_CANCELED;


class UserEditEmail_Presenter implements iUserEditEmail.iPresenter {

    private final static String TAG = "UserEditEmail_Presenter";

    private iUserEditEmail.iView view;

    private iUsersSingleton usersSingleton = UsersSingleton.getInstance();
    private String oldEmailAddress;
    private String newEmailAddress;

    private iUserEditEmail.ViewState currentViewState;
    private int currentMessageId;
    private String currentErrorDetails;
    private boolean isVirgin = true;


    @Override
    public void linkView(iUserEditEmail.iView view) {
        this.view = view;
    }

    @Override
    public void unlinkView() {
        this.view = new UserEmailEdit_ViewStub();
    }

    @Override
    public boolean isVirgin() {
        return isVirgin;
    }

    @Override
    public void onFirstOpen() {
        isVirgin = false;

        User user = usersSingleton.getCurrentUser();
        oldEmailAddress = user.getEmail();
        view.displayCurrentEmail(oldEmailAddress);
    }

    @Override
    public void onConfigChanged() {
        view.setViewState(currentViewState, currentMessageId, currentErrorDetails);
    }

    @Override
    public void onFormIsValid() {
        String newEmail = view.getEmail();
        String currentEmail = usersSingleton.getCurrentUser().getEmail();

        if (newEmail.equals(currentEmail)) {
            view.showEmailError(R.string.USER_EDIT_EMAIL_you_not_change_email_address);
            return;
        }

        checkEmail();
    }

    @Override
    public void onSaveButtonClicked() {
        view.validateForm();
    }

    @Override
    public void onCancelButtonClicked() {
        cancelEditing();
    }

    @Override
    public void onBackPressed() {
        cancelEditing();
    }

    @Override
    public boolean onHomePressed() {
        cancelEditing();
        return true;
    }

    @Override
    public void storeViewState(iUserEditEmail.ViewState state, int messageId, @Nullable String errorDetails) {
        this.currentViewState = state;
        this.currentMessageId = messageId;
        this.currentErrorDetails = errorDetails;
    }


    // Внутренние методы
    private void checkEmail() {
        String email = view.getEmail();
        String password = view.getPassword();

        view.setViewState(iUserEditEmail.ViewState.PROGRESS, R.string.checking_email);

        AuthSingleton.checkEmailExists(email, new iAuthSingleton.CheckEmailExistsCallbacks() {
            @Override
            public void onEmailExists() {
                view.setViewState(iUserEditEmail.ViewState.ERROR, R.string.USER_EDIT_EMAIL_error_email_elready_used);
            }

            @Override
            public void onEmailNotExists() {
                checkPassword(password);
            }

            @Override
            public void onEmailCheckError(String errorMsg) {
                view.setViewState(iUserEditEmail.ViewState.ERROR, R.string.USER_EDIT_EMAIL_error_checking_email, errorMsg);
            }
        });

        /*usersSingleton.checkEmailExists(email, new iUsersSingleton.CheckExistanceCallbacks() {
            @Override
            public void onCheckComplete() {

            }

            @Override
            public void onExists() {
                view.setViewState(iUserEditEmail.ViewState.EMAIL_ERROR, R.string.USER_EDIT_error_email_already_used);
            }

            @Override
            public void onNotExists() {
                String password = view.getPassword();
                checkPassword(password);
            }

            @Override
            public void onCheckFail(String errorMsg) {
                view.setViewState(iUserEditEmail.ViewState.ERROR, R.string.USER_EDIT_EMAIL_error_checking_email, errorMsg);
            }
        });*/
    }

    private void checkPassword(@NonNull String password) {
        view.setViewState(iUserEditEmail.ViewState.PROGRESS, R.string.checking_password);

        AuthSingleton.checkPassword(oldEmailAddress, password, new iAuthSingleton.CheckPasswordCallbacks() {
                @Override
                public void onUserCredentialsOk() {
                    updateEmail();
                }

                @Override
                public void onUserCredentialsNotOk(String errorMsg) {
                    view.setViewState(iUserEditEmail.ViewState.ERROR, R.string.error_wrong_password, errorMsg);
                }
            });
    }

    private void updateEmail() {
        String newEmail = view.getEmail();

        view.setViewState(iUserEditEmail.ViewState.PROGRESS, R.string.USER_EDIT_EMAIL_updating_email);

        usersSingleton.changeEmail(newEmail, new iUsersSingleton.ChangeEmailCallbacks() {
            @Override
            public void onEmailChangeSuccess() {
                view.setViewState(iUserEditEmail.ViewState.SUCCESS, R.string.USER_EDIT_EMAIL_email_successfully_updated);
            }

            @Override
            public void onEmailChangeError(String errorMsg) {
                view.setViewState(iUserEditEmail.ViewState.ERROR, R.string.USER_EDIT_EMAIL_error_updating_email);
            }
        });
    }


    private void cancelEditing() {
        view.closePage(RESULT_CANCELED, Intent.ACTION_EDIT);
    }
}
