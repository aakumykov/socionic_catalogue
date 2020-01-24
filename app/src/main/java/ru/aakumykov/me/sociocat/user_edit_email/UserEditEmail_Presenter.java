package ru.aakumykov.me.sociocat.user_edit_email;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.DeepLink_Constants;
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


    @Override
    public void linkView(iUserEditEmail.iView view) {
        this.view = view;
    }

    @Override
    public void unlinkView() {
        this.view = new UserEmailEdit_ViewStub();
    }

    @Override
    public void onFirstOpen() {
        User user = usersSingleton.getCurrentUser();
        oldEmailAddress = user.getEmail();
        view.displayCurrentEmail(oldEmailAddress);
    }

    @Override
    public void onConfigChanged() {
//        view.displayCurrentEmail(currentItem);
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
            view.showToast(R.string.USER_EDIT_EMAIL_you_not_change_email_address);
            return;
        }

        checkPassword(password);
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


    // Внутренние методы
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

                view.hideProgressBar();

                String message = MyUtils.getString(view.getAppContext(), R.string.USER_EDIT_EMAIL_verification_sent_message, newEmailAddress);

                MyDialogs.infoDialog(
                        view.getActivity(),
                        R.string.USER_EDIT_EMAIL_verification_sent_title,
                        message,
                        new iMyDialogs.StandardCallbacks() {
                            @Override
                            public void onCancelInDialog() {

                            }

                            @Override
                            public void onNoInDialog() {

                            }

                            @Override
                            public boolean onCheckInDialog() {
                                return false;
                            }

                            @Override
                            public void onYesInDialog() {
                                view.closePage(RESULT_OK, Intent.ACTION_EDIT);
                            }
                        }
                );
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
