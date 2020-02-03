package ru.aakumykov.me.sociocat.user_edit_email;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.interfaces.iMyDialogs;
import ru.aakumykov.me.sociocat.models.User;
import ru.aakumykov.me.sociocat.singletons.AuthSingleton;
import ru.aakumykov.me.sociocat.singletons.UsersSingleton;
import ru.aakumykov.me.sociocat.singletons.iAuthSingleton;
import ru.aakumykov.me.sociocat.singletons.iUsersSingleton;
import ru.aakumykov.me.sociocat.user_edit_email.stubs.UserEmailEdit_ViewStub;
import ru.aakumykov.me.sociocat.utils.MyDialogs;
import ru.aakumykov.me.sociocat.utils.MyUtils;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


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

//        view.setViewState(iUserEditEmail.ViewState.PROGRESS, );
    }

    @Override
    public void onSaveButtonClicked() {

        // Проверка формы
        boolean formHasError = false;

        newEmailAddress = view.getEmail().trim();
        if (!MyUtils.isCorrectEmail(newEmailAddress)) {
            view.showEmailError(R.string.VALIDATION_mailformed_email);
            formHasError = true;
        }

        String password = view.getPassword();
        if (TextUtils.isEmpty(password)) {
            view.showPasswordError(R.string.VALIDATION_field_required);
            formHasError = true;
        }

        if (formHasError)
            return;

        // Изменился ли Email ?
        if (null != oldEmailAddress && oldEmailAddress.equals(newEmailAddress)) {
            view.setViewState(iUserEditEmail.ViewState.EMAIL_ERROR, R.string.USER_EDIT_EMAIL_you_not_change_email_address);
            return;
        }

        checkEmail();
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

        view.setViewState(iUserEditEmail.ViewState.CHECKING, R.string.USER_EDIT_EMAIL_checking_email);

        usersSingleton.checkEmailExists(email, new iUsersSingleton.CheckExistanceCallbacks() {
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
                view.setViewState(iUserEditEmail.ViewState.PAGE_ERROR, R.string.USER_EDIT_EMAIL_error_checking_email, errorMsg);
            }
        });
    }

    private void checkPassword(@NonNull String password) {
        view.disableForm();
        view.showProgressMessage(R.string.checking_password);

        try {
            AuthSingleton.checkPassword(oldEmailAddress, password, new iAuthSingleton.CheckPasswordCallbacks() {
                @Override
                public void onUserCredentialsOk() {
                    sendVerificationEmail(newEmailAddress);
                }

                @Override
                public void onUserCredentialsNotOk(String errorMsg) {
                    view.enableForm();
                    view.showErrorMsg(R.string.error_wrong_password, errorMsg);
                }
            });
        }
        catch (iAuthSingleton.iAuthSingletonException e) {
            view.enableForm();
            view.showErrorMsg(R.string.USER_EDIT_EMAIL_cannot_check_password, e.getMessage());
            MyUtils.printError(TAG, e);
        }
    }

    private void sendVerificationEmail(@NonNull String newEmailAddress) {

        SharedPreferences sharedPreferences = view.getAppContext().getSharedPreferences(Constants.SHARED_PREFERENCES_USER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.KEY_STORED_EMAIL, newEmailAddress);
        editor.commit();

        String userId = AuthSingleton.currentUserId();

        view.disableForm();
        view.showProgressMessage(R.string.USER_EDIT_EMAIL_sending_confirmation_email);

        AuthSingleton.sendEmailChangeConfirmationLink(userId, newEmailAddress, new iAuthSingleton.SendSignInLinkCallbacks() {
            @Override
            public void onSignInLinkSendSuccess() {
                view.setViewState(iUserEditEmail.ViewState.SUCCESS, -1, newEmailAddress);
            }

            @Override
            public void onSignInLinkSendFail(String errorMsg) {
                view.enableForm();
                view.showErrorMsg(R.string.USER_EDIT_EMAIL_error_sending_confirmation, errorMsg);
            }
        });
    }

    private void cancelEditing() {
        view.closePage(RESULT_CANCELED, Intent.ACTION_EDIT);
    }
}
