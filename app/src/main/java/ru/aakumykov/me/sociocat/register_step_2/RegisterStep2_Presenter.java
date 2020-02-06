package ru.aakumykov.me.sociocat.register_step_2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.models.User;
import ru.aakumykov.me.sociocat.singletons.UsersSingleton;
import ru.aakumykov.me.sociocat.singletons.iUsersSingleton;

public class RegisterStep2_Presenter implements iRegisterStep2.Presenter {

    private iRegisterStep2.View view;

    private iUsersSingleton usersSingleton = UsersSingleton.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private boolean userNameIsValid = false;
    private boolean passwordIsValid = false;

    private boolean isVirgin = true;
    private iRegisterStep2.ViewState currentViewState;
    private int currentMessageId;
    private String currentMessageDetails;


    // Системные методы
    @Override
    public void linkView(iRegisterStep2.View view) {
        this.view = view;
    }

    @Override
    public void unlinkView() {
        this.view = new RegisterStep2_ViewStub();
    }

    @Override
    public void storeViewState(iRegisterStep2.ViewState viewState, int messageId, String messageDetails) {
        currentViewState = viewState;
        currentMessageId = messageId;
        currentMessageDetails = messageDetails;
    }

    @Override
    public boolean isVirgin() {
        return isVirgin;
    }

    @Override
    public void processInputIntent(@Nullable Intent intent) {
        isVirgin = false;

        if (null == intent) {
            view.setState(iRegisterStep2.ViewState.ERROR, R.string.REGISTER2_input_data_error, "There is no Intent data");
            return;
        }

        String action = intent.getAction() + "";
        if (TextUtils.isEmpty(action)) {
            view.setState(iRegisterStep2.ViewState.ERROR, R.string.REGISTER2_input_data_error, "There is no action in Intent");
            return;
        }

        switch (action) {
            case Constants.ACTION_CONTINUE_REGISTRATION:
                continueRegistration(intent);
                break;

            default:
                view.setState(iRegisterStep2.ViewState.ERROR, R.string.REGISTER2_input_data_error, "Unknown intent's action: "+action);
                break;
        }
    }

    @Override
    public void onConfigChanged() {

    }


    // Интерфейсные методы
    @Override
    public void processRegistration(Intent intent) {

        if (null == intent) {
            onErrorOccured(R.string.REGISTER2_input_data_error, "Intent is NULL");
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
            onErrorOccured(R.string.REGISTER2_input_data_error, e.getMessage());
            e.printStackTrace();
        }
    }

    private void checkNameByNetwork() {
        String userName = view.getUserName();

        view.showNameThrobber();

        try {
            usersSingleton.checkNameExists(userName, new iUsersSingleton.CheckExistanceCallbacks() {
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

            usersSingleton.createUser(userId, userName, email, new iUsersSingleton.CreateCallbacks() {
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

    private void continueRegistration(@NonNull Intent intent) {

        view.setState(iRegisterStep2.ViewState.PROGRESS, R.string.REGISTER2_continuing_registration);


    }
}
