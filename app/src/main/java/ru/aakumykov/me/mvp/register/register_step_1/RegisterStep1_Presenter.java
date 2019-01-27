package ru.aakumykov.me.mvp.register.register_step_1;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;

import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.utils.MyUtils;

public class RegisterStep1_Presenter implements iRegisterStep1.Presenter {

    private iRegisterStep1.View view;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();


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

        if (!isValidEmail()) return;

        final String email = view.getEmail();

        ActionCodeSettings actionCodeSettings =
                ActionCodeSettings.newBuilder()
                        .setUrl("https://sociocat.example.org/registration_step_2")
                        .setHandleCodeInApp(true)
                        .setAndroidPackageName(
                                Constants.PACKAGE_NAME,
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


    // Внутренние методы
    private boolean isValidEmail() {
        String email = view.getEmail();

        if (TextUtils.isEmpty(email)) {
            view.showEmailError(R.string.cannot_be_empty);
            return false;
        }

        if (!MyUtils.isEmailCorrect(email)) {
            view.showEmailError(R.string.REGISTER1_incorrect_email);
            return false;
        }

        return true;
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
