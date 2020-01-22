package ru.aakumykov.me.sociocat.user_edit_email;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;

import ru.aakumykov.me.sociocat.DeepLink_Constants;
import ru.aakumykov.me.sociocat.PackageConstants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.singletons.AuthSingleton;
import ru.aakumykov.me.sociocat.singletons.iAuthSingleton;
import ru.aakumykov.me.sociocat.user_edit_email.stubs.UserEmailEdit_ViewStub;
import ru.aakumykov.me.sociocat.utils.MyUtils;


class UserEditEmail_Presenter implements iUserEditEmail.iPresenter {

    private final static String TAG = "UserEditEmail_Presenter";

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

        String userId = AuthSingleton.currentUserId();

        ActionCodeSettings actionCodeSettings =
                ActionCodeSettings.newBuilder()
                        .setUrl(DeepLink_Constants.URL_BASE + DeepLink_Constants.CONFIRM_EMAIL_PATH + "?userId=" + userId)
                        .setHandleCodeInApp(true)
                        .setAndroidPackageName(
                                PackageConstants.PACKAGE_NAME,
                                true, /* Установить программу, если её нет */
                                PackageConstants.VERSION_NAME /* Минимальная версия */
                        )
                        .build();

        view.disableForm();
        view.showProgressMessage(R.string.USER_EDIT_EMAIL_sending_confirmation_email);

        FirebaseAuth.getInstance().sendPasswordResetEmail(email, actionCodeSettings)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        view.showInfoMsg(R.string.USER_EDIT_EMAIL_confirmation_link_is_sent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        view.enableForm();
                        view.showErrorMsg(R.string.USER_EDIT_EMAIL_error_sending_confirmation, e.getMessage());
                        MyUtils.printError(TAG, e);
                    }
                });
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
