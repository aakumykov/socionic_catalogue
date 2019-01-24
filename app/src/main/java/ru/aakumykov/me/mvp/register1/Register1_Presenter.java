package ru.aakumykov.me.mvp.register1;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;

import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.R;

public class Register1_Presenter implements iRegister1.Presenter {

    private iRegister1.View view;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();


    // Системные методы
    @Override
    public void linkView(iRegister1.View view) {
        this.view = view;
    }

    @Override
    public void unlinkView() {
        this.view = null;
    }

    // Интерфейсные методы
    @Override
    public void sendRegistrationEmail() {

        String email = view.getEmail();

        ActionCodeSettings actionCodeSettings =
                ActionCodeSettings.newBuilder()
                        .setUrl("https://sociocat.example.org/registration_step_2")
                        .setHandleCodeInApp(true)
                        .setAndroidPackageName(
                                Constants.PACKAGE_NAME,
                                true, /* installIfNotAvailable */
                                null    /* minimumVersion */)
                        .build();

        view.showProgressMessage(R.string.REGISTER1_sending_email);

        firebaseAuth.sendSignInLinkToEmail(email, actionCodeSettings)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        view.hideProgressBar();
                        view.hideMsg();
                        view.showSuccessDialog();
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
    private void onErrorOccured(int userMsgId, String adminErrorMsg) {
        view.showErrorMsg(userMsgId, adminErrorMsg);
        view.enableForm();
    }
}
