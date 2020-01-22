package ru.aakumykov.me.sociocat.user_edit_email;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.dropbox.core.android.Auth;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ru.aakumykov.me.sociocat.DeepLink_Constants;
import ru.aakumykov.me.sociocat.PackageConstants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.models.User;
import ru.aakumykov.me.sociocat.singletons.AuthSingleton;
import ru.aakumykov.me.sociocat.singletons.UsersSingleton;
import ru.aakumykov.me.sociocat.singletons.iAuthSingleton;
import ru.aakumykov.me.sociocat.singletons.iUsersSingleton;
import ru.aakumykov.me.sociocat.user_edit_email.stubs.UserEmailEdit_ViewStub;
import ru.aakumykov.me.sociocat.utils.MyUtils;


class UserEditEmail_Presenter implements iUserEditEmail.iPresenter {

    private final static String TAG = "UserEditEmail_Presenter";

    private iUserEditEmail.iView view;
    private iAuthSingleton authSingleton = AuthSingleton.getInstance();
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

        // Проверка пароля
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


    // Внутренние методы
    private void sendVerificationEmail(@NonNull String newEmailAddress) {

        String userId = AuthSingleton.currentUserId();

        view.disableForm();
        view.showProgressMessage(R.string.USER_EDIT_EMAIL_sending_confirmation_email);

        AuthSingleton.sendSignInLinkToEmail(userId, newEmailAddress, new iAuthSingleton.SendSignInLinkCallbacks() {
            @Override
            public void onSignInLinkSendSuccess() {
                view.showInfoMsg(R.string.USER_EDIT_EMAIL_confirmation_link_is_sent);
            }

            @Override
            public void onSignInLinkSendFail(String errorMsg) {
                view.enableForm();
                view.showErrorMsg(R.string.USER_EDIT_EMAIL_error_sending_confirmation, errorMsg);
            }
        });
    }

    @Override
    public void onCancelButtonClicked() {
        view.closePage();
    }

    @Override
    public void onBackPressed() {
        view.closePage();
    }

    @Override
    public boolean onHomePressed() {
        view.closePage();
        return true;
    }
}
