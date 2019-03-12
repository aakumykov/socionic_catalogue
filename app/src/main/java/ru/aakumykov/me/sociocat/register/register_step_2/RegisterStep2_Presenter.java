package ru.aakumykov.me.sociocat.register.register_step_2;

import android.content.Intent;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import android.text.TextUtils;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.interfaces.iUsersSingleton;
import ru.aakumykov.me.sociocat.models.User;
import ru.aakumykov.me.sociocat.services.UsersSingleton;

public class RegisterStep2_Presenter implements iRegisterStep2.Presenter {

    private iRegisterStep2.View view;
    private iUsersSingleton usersService = UsersSingleton.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private boolean userNameIsValid = false;
    private boolean passwordIsValid = false;

    // Системные методы
    @Override
    public void linkView(iRegisterStep2.View view) {
        this.view = view;
    }

    @Override
    public void unlinkView() {
        this.view = null;
    }


    // Интерфейсные методы
    @Override
    public void processRegistration(Intent intent) {

        if (null == intent) {
            onErrorOccured(R.string.REGISTER2_wrong_input_data, "Intent is NULL");
            return;
        }

        if (formValidLocally()) {
            signInWithEmailedLink(intent);
            checkNameByNetwork();
        }
    }


    // Внутренние методы
    private boolean formValidLocally() {
        return checkPassword() && checkUserNameLocal();
    }

    private boolean checkUserNameLocal() {
        String userName = view.getUserName();

        if (TextUtils.isEmpty(userName)) {
            view.showUserNameError(R.string.cannot_be_empty);
            return false;
        }

        if (userName.length() < Constants.USER_NAME_MIN_LENGTH) {
            view.showUserNameError(R.string.REGISTER2_user_name_too_short);
            return false;
        }

        if (userName.length() > Constants.USER_NAME_MAX_LENGTH) {
            view.showUserNameError(R.string.REGISTER2_user_name_too_long);
            return false;
        }

        return true;
    }

    private boolean checkPassword() {
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

    private void signInWithEmailedLink(Intent intent) {
        try {
            String emailSignInURI = intent.getStringExtra("emailSignInURL");

            if (firebaseAuth.isSignInWithEmailLink(emailSignInURI)) {

                SharedPreferences sharedPreferences = view.getSharedPrefs(Constants.SHARED_PREFERENCES_EMAIL);

                if (sharedPreferences.contains("email")) {
                    final String storedEmail = sharedPreferences.getString("email", null);

                    firebaseAuth.signInWithEmailLink(storedEmail, emailSignInURI)
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    checkNameByNetwork();
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

            } else {
                onErrorOccured(R.string.REGISTER2_registration_error, "Cannot sign in with email link");
            }

        } catch (Exception e) {
            onErrorOccured(R.string.REGISTER2_wrong_input_data, e.getMessage());
            e.printStackTrace();
        }
    }

    private void checkNameByNetwork() {
        String userName = view.getUserName();

        view.showNameThrobber();

        try {
            usersService.checkNameExists(userName, new iUsersSingleton.CheckExistanceCallbacks() {
                @Override
                public void onCheckComplete() {
                }

                @Override
                public void onExists() {
                    view.hideNameThrobber();
                    view.showUserNameError(R.string.REGISTER2_user_name_already_used);
                }

                @Override
                public void onNotExists() {
                    try {
                        String userId = firebaseAuth.getUid();
                        String email = firebaseAuth.getCurrentUser().getEmail();
                        createAppUser(userId, email);

                    } catch (Exception e) {
                        onErrorOccured(R.string.REGISTER2_registration_error, e.getMessage());
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCheckFail(String errorMsg) {
                    view.hideNameThrobber();
                    onErrorOccured(R.string.REGISTER2_user_name_check_error, errorMsg);
                }
            });

        } catch (Exception e) {
            view.enableForm();
            onErrorOccured(R.string.REGISTER2_registration_error, e.getMessage());
            e.printStackTrace();
        }
    }

    private void createAppUser(String userId, String email) {
        try {
            view.disableForm();
            view.showProgressMessage(R.string.REGISTER2_registration_in_progress);

            String userName = view.getUserName();

            usersService.createUser(userId, userName, email, new iUsersSingleton.CreateCallbacks() {
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
