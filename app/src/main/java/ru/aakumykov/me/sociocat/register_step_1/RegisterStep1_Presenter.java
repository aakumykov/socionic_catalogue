package ru.aakumykov.me.sociocat.register_step_1;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;

import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.DeepLink_Constants;
import ru.aakumykov.me.sociocat.PackageConstants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.singletons.UsersSingleton;
import ru.aakumykov.me.sociocat.singletons.iUsersSingleton;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class RegisterStep1_Presenter implements iRegisterStep1.Presenter {

    private iRegisterStep1.View view;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private iUsersSingleton usersSingleton = UsersSingleton.getInstance();
    private iRegisterStep1.ViewStatus viewStatus;
    private String errorMessage = null;


    // Системные методы
    @Override
    public void linkView(iRegisterStep1.View view) {
        this.view = view;
    }

    @Override
    public void unlinkView() {
        this.view = new RegisterStep1_View();
    }

    @Override
    public boolean isVirgin() {
        return null == viewStatus;
    }

    @Override
    public void storeViewStatus(iRegisterStep1.ViewStatus viewStatus, String errorMessage) {
        this.viewStatus = viewStatus;
        this.errorMessage = errorMessage;
    }

    @Override
    public void onConfigChanged() {
        view.setStatus(viewStatus, errorMessage);
    }


    // Интерфейсные методы
    @Override
    public void doInitialCheck() {
        if (null != firebaseAuth.getCurrentUser()) {
            view.accessDenied(R.string.REGISTER1_you_are_already_authorized);
        }
    }

    @Override
    public void produceRegistrationStep1() {
        checkEmail();
    }


    // Внутренние методы
    private void checkEmail() {
        String email = view.getEmail();

        if (TextUtils.isEmpty(email)) {
            view.setStatus(iRegisterStep1.ViewStatus.EMAIL_ERROR, R.string.cannot_be_empty);
            return;
        }

        if (!MyUtils.isCorrectEmail(email)) {
            view.setStatus(iRegisterStep1.ViewStatus.EMAIL_ERROR, R.string.REGISTER1_incorrect_email);
            return;
        }


        view.setStatus(iRegisterStep1.ViewStatus.CHECKING, null);

        usersSingleton.checkEmailExists(email, new iUsersSingleton.CheckExistanceCallbacks() {
            @Override
            public void onCheckComplete() {

            }

            @Override
            public void onExists() {
                view.setStatus(iRegisterStep1.ViewStatus.EMAIL_ERROR, R.string.REGISTER1_email_already_used);
            }

            @Override
            public void onNotExists() {
                view.setStatus(iRegisterStep1.ViewStatus.SUCCESS, null);
                step1_sendRegistrationEmail();
            }

            @Override
            public void onCheckFail(String errorMsg) {
                view.setStatus(iRegisterStep1.ViewStatus.COMMON_ERROR, errorMsg);
            }
        });
    }

    private void step1_sendRegistrationEmail() {
        final String email = view.getEmail();

        String continueUrl =
                DeepLink_Constants.URL_BASE
                        + DeepLink_Constants.PATH_REGISTRATION_STEP2
                        + DeepLink_Constants.KEY_ACTION + "=" + DeepLink_Constants.ACTION_CONTINUE_REGISTRATION;

        ActionCodeSettings actionCodeSettings =
                ActionCodeSettings.newBuilder()
                        .setUrl(continueUrl)
                        .setHandleCodeInApp(true)
                        .setAndroidPackageName(
                                PackageConstants.PACKAGE_NAME,
                                true, /* installIfNotAvailable */
                                null    /* minimumVersion */)
                        .build();

        view.disableForm();
        view.showProgressMessage(R.string.REGISTER1_sending_email);

        firebaseAuth.sendSignInLinkToEmail(email, actionCodeSettings)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        view.hideProgressMessage();
                        view.showSuccessDialog();
                        storeEmailLocally(email);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        view.enableForm();
                        onErrorOccurred(R.string.REGISTER1_error_sending_email, e.getMessage());
                        e.printStackTrace();
                    }
                });
    }

    private void storeEmailLocally(String email) {
        SharedPreferences sharedPreferences =
                view.getAppContext().getSharedPreferences(Constants.SHARED_PREFERENCES_USER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.KEY_STORED_EMAIL, email);
        editor.apply();
    }

    private void onErrorOccurred(int userMsgId, String adminErrorMsg) {
        view.showErrorMsg(userMsgId, adminErrorMsg);
        view.enableForm();
    }
}
