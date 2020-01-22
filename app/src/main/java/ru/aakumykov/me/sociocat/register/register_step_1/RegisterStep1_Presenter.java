package ru.aakumykov.me.sociocat.register.register_step_1;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;

import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.DynamicLink_Constants;
import ru.aakumykov.me.sociocat.PackageConstants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.singletons.UsersSingleton;
import ru.aakumykov.me.sociocat.singletons.iUsersSingleton;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class RegisterStep1_Presenter implements iRegisterStep1.Presenter {

    private iRegisterStep1.View view;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private iUsersSingleton usersSingleton = UsersSingleton.getInstance();


    // Системные методы
    @Override
    public void linkView(iRegisterStep1.View view) {
        this.view = view;
    }

    @Override
    public void unlinkView() {
        this.view = null;
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
            view.showEmailError(R.string.cannot_be_empty);
            return;
        }

        if (!MyUtils.isCorrectEmail(email)) {
            view.showEmailError(R.string.REGISTER1_incorrect_email);
            return;
        }


        view.showEmailChecked();

        usersSingleton.checkEmailExists(email, new iUsersSingleton.CheckExistanceCallbacks() {
            @Override
            public void onCheckComplete() {
                view.hideEmailChecked();
            }

            @Override
            public void onExists() {
                view.showEmailError(R.string.REGISTER1_email_already_used);
            }

            @Override
            public void onNotExists() {
                step1_sendRegistrationEmail();
            }

            @Override
            public void onCheckFail(String errorMsg) {
                view.showEmailError(R.string.REGISTER1_error_check_email);
            }
        });
    }

    private void step1_sendRegistrationEmail() {
        final String email = view.getEmail();

        ActionCodeSettings actionCodeSettings =
                ActionCodeSettings.newBuilder()
                        .setUrl(DynamicLink_Constants.ACTION_URL_BASE + DynamicLink_Constants.REGISTRATION_STEP2_PATH)
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
                        onErrorOccured(R.string.REGISTER1_error_sending_email, e.getMessage());
                        e.printStackTrace();
                    }
                });
    }

    private void storeEmailLocally(String email) {
        SharedPreferences sharedPreferences =
                view.getAppContext().getSharedPreferences(Constants.SHARED_PREFERENCES_EMAIL, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", email);
        editor.apply();
    }

    private void onErrorOccured(int userMsgId, String adminErrorMsg) {
        view.showErrorMsg(userMsgId, adminErrorMsg);
        view.enableForm();
    }
}
