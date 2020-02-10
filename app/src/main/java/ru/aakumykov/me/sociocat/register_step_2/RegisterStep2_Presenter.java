package ru.aakumykov.me.sociocat.register_step_2;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.singletons.AuthSingleton;
import ru.aakumykov.me.sociocat.singletons.iAuthSingleton;

public class RegisterStep2_Presenter implements iRegisterStep2.Presenter {

    private static final String TAG = "RegisterStep2_Presenter";

    private iRegisterStep2.View view;

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

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

        continueRegistration();
    }

    @Override
    public void onConfigChanged() {
        view.setState(currentViewState, currentMessageId, currentMessageDetails);
    }

    @Override
    public void onFormIsValid() {
        setPassword();
    }

    @Override
    public void onCancelRequested() {
        pageLeaveRequested();
    }


    // Внутренние методы
    private void continueRegistration() {
        String email = AuthSingleton.emailOfCurrentUser();
        view.displayInstructions(email);
        view.setState(iRegisterStep2.ViewState.INITIAL, -1);
    }

    /*private void createTempUserName() {

        showProgress(R.string.REGISTER2_preparing_user_account);

        this.tempUserName = MVPUtils.tempUserName(view.getAppContext());

        usersSingleton.checkNameExists(tempUserName, new iUsersSingleton.CheckExistanceCallbacks() {
            @Override
            public void onCheckComplete() {

            }

            @Override
            public void onExists() {
                createTempUserName();
            }

            @Override
            public void onNotExists() {
                createTempUser(tempUserName);
            }

            @Override
            public void onCheckFail(String errorMsg) {
                showError(R.string.REGISTER2_error_preparing_user_account, errorMsg);
                Log.e(TAG, "Error checking user name existence: '"+tempUserName+"'");
                AuthSingleton.logout();
            }
        });
    }*/

    /*private void createTempUser(@NonNull String tempUserName) {

        showProgress(R.string.REGISTER2_preparing_user_account);

        String userId = AuthSingleton.currentUserId();
        String email = AuthSingleton.emailOfCurrentUser();

        usersSingleton.createUser(userId, tempUserName, email, new iUsersSingleton.CreateCallbacks() {
            @Override
            public void onUserCreateSuccess(User user) {
                usersSingleton.storeCurrentUser(user);

                view.setState(iRegisterStep2.ViewState.INITIAL, -1);
                view.displayEmail(user.getName());
            }

            @Override
            public void onUserCreateFail(String errorMsg) {
                showError(R.string.REGISTER2_error_preparing_user_account, errorMsg);
                AuthSingleton.logout();
            }
        });
    }*/

    /*private void checkUserName() {

        view.setState(iRegisterStep2.ViewState.CHECKING_USER_NAME, -1);

        String userName = view.getUserName();

        if (userName.equals(tempUserName)) {
            setPassword();
        }
        else {
            usersSingleton.checkNameExists(view.getUserName(), new iUsersSingleton.CheckExistanceCallbacks() {
                @Override
                public void onCheckComplete() {

                }

                @Override
                public void onExists() {
                    view.setState(iRegisterStep2.ViewState.NAME_ERROR, R.string.REGISTER2_user_name_already_used);
                }

                @Override
                public void onNotExists() {
                    setPassword();
                }

                @Override
                public void onCheckFail(String errorMsg) {
                    showError(R.string.REGISTER2_error_checking_user_name, errorMsg);
                }
            });
        }
    }*/

    private void setPassword() {

        showProgress(R.string.REGISTER2_creating_user);

        AuthSingleton.changePassword(view.getPassword(), new iAuthSingleton.ChangePasswordCallbacks() {
            @Override
            public void onChangePasswordSuccess() {
                view.setState(iRegisterStep2.ViewState.SUCCESS, R.string.REGISTER2_registration_success);
            }

            @Override
            public void onChangePasswordError(String errorMsg) {
                showError(R.string.REGISTER2_error_setting_password, errorMsg);
            }
        });
    }

    /*private void updateUser() {

        showProgress(R.string.REGISTER2_creating_user);

        User user = usersSingleton.getCurrentUser();
        user.setName(view.getUserName());

        usersSingleton.saveUser(user, new iUsersSingleton.SaveCallbacks() {
            @Override
            public void onUserSaveSuccess(User user) {
                view.setState(iRegisterStep2.ViewState.SUCCESS, R.string.REGISTER2_registration_success);
            }

            @Override
            public void onUserSaveFail(String errorMsg) {
                showError(R.string.REGISTER2_error_creating_user, errorMsg);
            }
        });
    }*/

    private void showProgress(int messageId) {
        view.setState(iRegisterStep2.ViewState.PROGRESS, messageId);
    }

    private void showError(int messageId, String messageDetails) {
        view.setState(iRegisterStep2.ViewState.ERROR, messageId, messageDetails);
        Log.e(TAG, messageDetails);
    }

    private void pageLeaveRequested() {
        view.confirmPageLeave();
    }
}
