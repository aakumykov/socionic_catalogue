package ru.aakumykov.me.mvp.register2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.interfaces.iUsersSingleton;
import ru.aakumykov.me.mvp.models.User;
import ru.aakumykov.me.mvp.services.UsersSingleton;

public class Register2_Presenter implements iRegister2.Presenter {

    private iRegister2.View view;
    private iUsersSingleton usersService = UsersSingleton.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    // Системные методы
    @Override
    public void linkView(iRegister2.View view) {
        this.view = view;
    }

    @Override
    public void unlinkView() {
        this.view = null;
    }


    // Интерфейсные методы
    @Override
    public void finishRegistration(Intent intent) {

        if (!isValidPassword()) {
            return;
        }

        if (null == intent) {
            onErrorOccured(R.string.REGISTER2_wrong_input_data, "Intent is NULL");
            return;
        }

        try {
            view.hideUserMessage();
            String url = intent.getStringExtra("emailSignInURL");
            if (firebaseAuth.isSignInWithEmailLink(url)) {
                createFirebaseUser(url);
            } else {
                onErrorOccured(R.string.REGISTER2_registration_error, "Cannot sign in with email link");
            }

        } catch (Exception e) {
            onErrorOccured(R.string.REGISTER2_wrong_input_data, e.getMessage());
            e.printStackTrace();
        }
    }


    // Внутренние методы
    private boolean isValidPassword() {
        String password1 = view.getPassword1();
        String password2 = view.getPassword2();

        if (TextUtils.isEmpty(password1)) {
            view.showPassword1Error(R.string.cannot_be_empty);
            return false;
        }

        if (TextUtils.isEmpty(password2)) {
            view.showPassword2Error(R.string.cannot_be_empty);
            return false;
        }

        if (!password1.equals(password2)) {
            view.showPassword1Error(R.string.REGISTER2_passwords_mismatch);
            view.showPassword2Error(R.string.REGISTER2_passwords_mismatch);
            return false;
        }

        if (password1.length() < Constants.PASSWORD_MIN_LENGTH) {
            view.showPassword1Error(R.string.REGISTER2_password_is_too_short);
            view.showPassword2Error(R.string.REGISTER2_password_is_too_short);
            return false;
        }

        return true;
    }

    private void createFirebaseUser(String emailSignInURI) {
        try {
            SharedPreferences sharedPreferences = view.getSharedPrefs(Constants.SHARED_PREFERENCES_EMAIL);
            if (sharedPreferences.contains("email")) {

                final String storedEmail = sharedPreferences.getString("email", null);

                view.showProgressMessage(R.string.REGISTER2_registration_in_progress);

                firebaseAuth.signInWithEmailLink(storedEmail, emailSignInURI)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                String userId = authResult.getUser().getUid();

                                createAppUser(userId, storedEmail);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                onErrorOccured(R.string.REGISTER2_registration_error, e.getMessage());
                                e.printStackTrace();
                            }
                        });

            } else {
                onErrorOccured(R.string.REGISTER2_registration_error, "Locally stored email not found");
            }

        } catch (Exception e) {
            onErrorOccured(R.string.REGISTER2_registration_error, e.getMessage());
            e.printStackTrace();
        }
    }

    private void createAppUser(String userId, String email) {
        try {
            view.showProgressMessage(R.string.REGISTER2_creating_user);

            usersService.createUser(userId, "", email, new iUsersSingleton.CreateCallbacks() {
                @Override
                public void onUserCreateSuccess(User user) {
                    setUserPassword();
                }

                @Override
                public void onUserCreateFail(String errorMsg) {
                    onErrorOccured(R.string.REGISTER2_registration_error, errorMsg);
                }
            });

        } catch (Exception e) {
            onErrorOccured(R.string.REGISTER2_registration_error, e.getMessage());
            e.printStackTrace();
        }
    }

    private void setUserPassword() {
        try {

            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

            String email = firebaseUser.getEmail();
            String password = view.getPassword1();

            firebaseUser.updatePassword(password)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            view.hideProgressMessage();
                            view.showToast(R.string.REGISTER2_registration_success);
                            view.goMainPage();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            onErrorOccured(R.string.REGISTER2_registration_error, e.getMessage());
                            e.printStackTrace();
                        }
                    });

//            EmailAuthCredential emailAuthCredential =
//                    (EmailAuthCredential) EmailAuthProvider.getCredential(email, password);
//
//            firebaseUser.linkWithCredential(emailAuthCredential)
//                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
//                        @Override
//                        public void onSuccess(AuthResult authResult) {
//
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//
//                        }
//                    });

        } catch (Exception e) {
            onErrorOccured(R.string.REGISTER2_registration_error, e.getMessage());
            e.printStackTrace();
        }
    }

    // TODO: удалять FirebaseUser ...

    private void onErrorOccured(int userMsgId, String adminErrorMsg) {
        view.showErrorMsg(userMsgId, adminErrorMsg);
        view.enableForm();
    }
}
