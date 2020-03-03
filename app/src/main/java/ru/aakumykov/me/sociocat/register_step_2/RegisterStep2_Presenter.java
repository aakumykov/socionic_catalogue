package ru.aakumykov.me.sociocat.register_step_2;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.AppConfig;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.models.User;
import ru.aakumykov.me.sociocat.singletons.AuthSingleton;
import ru.aakumykov.me.sociocat.singletons.UsersSingleton;
import ru.aakumykov.me.sociocat.singletons.iAuthSingleton;
import ru.aakumykov.me.sociocat.singletons.iUsersSingleton;
import ru.aakumykov.me.sociocat.utils.MVPUtils.MVPUtils;

public class RegisterStep2_Presenter implements iRegisterStep2.Presenter {

    private static final String TAG = "RegisterStep2_Presenter";

    private iRegisterStep2.View view;
    private iRegisterStep2.ViewState currentViewState;
    private int currentMessageId;
    private String currentMessageDetails;
    private boolean isVirgin = true;
    private iUsersSingleton usersSingleton = UsersSingleton.getInstance();

    private int createTempUserNameAttemptNumber = 0;


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
    public void storeViewState(iRegisterStep2.ViewState viewState, int messageId, String messageDetails) {
        currentViewState = viewState;
        currentMessageId = messageId;
        currentMessageDetails = messageDetails;
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
        createTempUserName();
    }

    private void createTempUserName() {

        showProgress(R.string.REGISTER2_preparing_user_account);

        String tempUserName = MVPUtils.tempUserName(view.getAppContext());

        if (++createTempUserNameAttemptNumber > AppConfig.CREATE_TEMP_USER_NAME_TRIES_COUNT) {
            showFatalError(R.string.REGISTER2_error_preparing_user_account, "Cannot create temporary user name");
            return;
        }

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
                showFatalError(R.string.REGISTER2_error_preparing_user_account, errorMsg);
                Log.e(TAG, "Error checking user name existence ("+tempUserName+"): "+errorMsg);
            }
        });
    }

    private void createTempUser(@NonNull String tempUserName) {

        showProgress(R.string.REGISTER2_preparing_user_account);

        String userId = AuthSingleton.currentUserId();
        String email = AuthSingleton.emailOfCurrentUser();

        usersSingleton.createUser(userId, tempUserName, email, new iUsersSingleton.CreateCallbacks() {
            @Override
            public void onUserCreateSuccess(User user) {
                usersSingleton.storeCurrentUser(user);
                view.setState(iRegisterStep2.ViewState.INITIAL, -1, user.getEmail());
            }

            @Override
            public void onUserCreateFail(String errorMsg) {
                showErrorAndLogout(R.string.REGISTER2_error_preparing_user_account, errorMsg);
            }
        });
    }

    /*private void checkUserName() {

        view.setState(iRegisterStep2.ViewStates.CHECKING_USER_NAME, -1);

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
                    view.setState(iRegisterStep2.ViewStates.NAME_ERROR, R.string.REGISTER2_user_name_already_used);
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

        showProgress(R.string.REGISTER2_setting_password);

        AuthSingleton.changePassword(view.getPassword(), new iAuthSingleton.ChangePasswordCallbacks() {
            @Override
            public void onChangePasswordSuccess() {
                view.setState(iRegisterStep2.ViewState.SUCCESS, -1);
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
                view.setState(iRegisterStep2.ViewStates.SUCCESS, R.string.REGISTER2_registration_success);
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

    private void showErrorAndLogout(int messageId, String messageDetails) {
        showError(messageId, messageDetails);
        Log.e(TAG, messageDetails);
        AuthSingleton.logout();
    }

    private void showFatalError(int messageId, String messageDetails) {
        view.setState(iRegisterStep2.ViewState.FATAL_ERROR, R.string.REGISTER2_error_preparing_user_account, messageDetails);
        Log.e(TAG, messageDetails);
        AuthSingleton.logout();
    }

    private void pageLeaveRequested() {
        view.confirmPageLeave();
    }
}
