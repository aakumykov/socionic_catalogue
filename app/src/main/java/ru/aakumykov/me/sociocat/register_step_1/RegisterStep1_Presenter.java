package ru.aakumykov.me.sociocat.register_step_1;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;

import io.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.DeepLink_Constants;
import ru.aakumykov.me.sociocat.PackageConstants;
import ru.aakumykov.me.sociocat.constants.Constants;
import ru.aakumykov.me.sociocat.singletons.UsersSingleton;
import ru.aakumykov.me.sociocat.singletons.iUsersSingleton;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class RegisterStep1_Presenter implements iRegisterStep1.Presenter {

    private static final String TAG = "RegisterStep1_Presenter";
    private iRegisterStep1.View view;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private iUsersSingleton usersSingleton = UsersSingleton.getInstance();
    private iRegisterStep1.ViewStates currentViewStates;
    private int currentMessageId;
    private String currentErrorMessage = null;


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
        return null == currentViewStates;
    }

    @Override
    public void storeViewStatus(iRegisterStep1.ViewStates viewStatus, int messageId, String errorMessage) {
        this.currentViewStates = viewStatus;
        this.currentMessageId = messageId;
        this.currentErrorMessage = errorMessage;
    }

    @Override
    public void onConfigChanged() {
        view.setState(currentViewStates, currentMessageId, currentErrorMessage);
    }


    // Интерфейсные методы
    @Override
    public void doInitialCheck() {
        if (null != firebaseAuth.getCurrentUser()) {
            view.accessDenied(R.string.REGISTER1_you_are_already_authorized);
        }
    }

    @Override
    public void onRegisterButtonClicked() {
        checkEmail();
    }


    // Внутренние методы
    private void checkEmail() {
        String email = view.getEmail();

        if (TextUtils.isEmpty(email)) {
            view.setState(iRegisterStep1.ViewStates.EMAIL_ERROR, R.string.cannot_be_empty);
            return;
        }

        if (!MyUtils.isCorrectEmail(email)) {
            view.setState(iRegisterStep1.ViewStates.EMAIL_ERROR, R.string.REGISTER1_incorrect_email);
            return;
        }


        view.setState(iRegisterStep1.ViewStates.CHECKING, -1, null);

        usersSingleton.checkEmailExists(email, new iUsersSingleton.iCheckExistanceCallbacks() {
            @Override
            public void onCheckComplete() {

            }

            @Override
            public void onExists() {
                view.setState(iRegisterStep1.ViewStates.EMAIL_ERROR, R.string.REGISTER1_email_already_used);
            }

            @Override
            public void onNotExists() {
                view.setState(iRegisterStep1.ViewStates.SUCCESS, -1, null);
                sendRegistrationEmail();
            }

            @Override
            public void onCheckFail(String errorMsg) {
                view.setState(iRegisterStep1.ViewStates.COMMON_ERROR, R.string.REGISTER1_error_checking_email, errorMsg);
            }
        });
    }

    private void sendRegistrationEmail() {
        final String email = view.getEmail();

        String continueUrl =
                DeepLink_Constants.URL_BASE
                        + DeepLink_Constants.PATH_REGISTRATION_STEP2
                        + "?"
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

        view.setState(iRegisterStep1.ViewStates.PROGRESS, R.string.REGISTER1_sending_email);

        // TODO: перенести в AuthSingleton

        firebaseAuth.sendSignInLinkToEmail(email, actionCodeSettings)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        view.setState(iRegisterStep1.ViewStates.SUCCESS, R.string.REGISTER1_email_successfully_sent);
                        view.showSuccessDialog();
                        storeEmailLocally(email);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        view.setState(iRegisterStep1.ViewStates.COMMON_ERROR,R.string.REGISTER1_error_sending_email, e.getMessage());
                        MyUtils.printError(TAG, e);
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
}
