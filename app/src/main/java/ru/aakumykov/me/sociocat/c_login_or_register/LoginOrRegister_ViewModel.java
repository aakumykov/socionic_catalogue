package ru.aakumykov.me.sociocat.c_login_or_register;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.a_basic_mvvm_page_components.BasicMVVMPage_ViewModel;
import ru.aakumykov.me.sociocat.a_basic_mvvm_page_components.page_event.ToastPageEvent;
import ru.aakumykov.me.sociocat.a_basic_mvvm_page_components.page_state.ErrorPageState;
import ru.aakumykov.me.sociocat.a_basic_mvvm_page_components.page_state.NeutralPageState;
import ru.aakumykov.me.sociocat.a_basic_mvvm_page_components.page_state.ProgressPageState;
import ru.aakumykov.me.sociocat.c_login_or_register.page_events.LogoutClickedEvent;
import ru.aakumykov.me.sociocat.c_login_or_register.page_events.RegisterClikcedPageEvent;
import ru.aakumykov.me.sociocat.c_login_or_register.page_states.LoggedInPageState;
import ru.aakumykov.me.sociocat.c_login_or_register.page_states.LoggedOutPageState;
import ru.aakumykov.me.sociocat.c_login_or_register.page_states.LoginErrorPageState;
import ru.aakumykov.me.sociocat.c_login_or_register.page_states.LoginSuccessPageEvent;
import ru.aakumykov.me.sociocat.c_login_or_register.page_states.LoginWithEmailAndPasswordEvent;
import ru.aakumykov.me.sociocat.c_login_or_register.page_states.LoginWithGoogleEvent;
import ru.aakumykov.me.sociocat.c_login_or_register.page_states.LoginWithVKEvent;
import ru.aakumykov.me.sociocat.models.User;
import ru.aakumykov.me.sociocat.singletons.AuthSingleton;
import ru.aakumykov.me.sociocat.singletons.UsersSingleton;
import ru.aakumykov.me.sociocat.singletons.iAuthSingleton;
import ru.aakumykov.me.sociocat.singletons.iUsersSingleton;
import ru.aakumykov.me.sociocat.utils.auth.GoogleAuthHelper;

public class LoginOrRegister_ViewModel extends BasicMVVMPage_ViewModel {

    private static final String TAG = LoginOrRegister_ViewModel.class.getSimpleName();
    private final iUsersSingleton mUsersSingleton = UsersSingleton.getInstance();

    @Override
    protected void onColdStart() {
        if (AuthSingleton.isLoggedIn())
            setLoggedInPageState();
        else
            setLoggedOutPageState();
    }


    // События пользовательского ввода
    public void onRegisterButtonClicked() {
        risePageEvent(new RegisterClikcedPageEvent());
    }

    public void onLoginWithEmailAndPasswordClicked() {
        risePageEvent(new LoginWithEmailAndPasswordEvent());
    }

    public void onLoginWithGoogleClicked() {
        setPageState(new ProgressPageState(R.string.LOGIN_logging_in));
        risePageEvent(new LoginWithGoogleEvent());
    }

    public void onLoginWithVKClicked() {
        risePageEvent(new LoginWithVKEvent());
    }

    public void onLogoutButtonClicked() {
        risePageEvent(new LogoutClickedEvent());
    }


    // Глобальные события аутентификации
    public void onUserLoggedIn() {
        setLoggedInPageState();
    }

    public void onUserLoggedOut() {
        setLoggedOutPageState();
    }


    // События аутентификации Google
    public void onLoginWithGoogleConfirmed(@Nullable Intent data) {

        GoogleAuthHelper.processGoogleLoginResult(data, new GoogleAuthHelper.iGoogleLoginCallbacks() {
            @Override
            public void onGoogleLoginSuccess(@NonNull GoogleSignInAccount googleSignInAccount) {
                loginWithGoogleAccount(googleSignInAccount);
            }

            @Override
            public void onGoogleLoginError(String errorMsg) {
                setPageState(new ErrorPageState(TAG, R.string.LOGIN_login_error, errorMsg));
            }
        });
    }

    public void onLoginWithGoogleCancelled() {
        risePageEvent(new ToastPageEvent(R.string.LOGIN_login_has_been_cancelled));
        setPageState(new NeutralPageState());
    }

    public void onLoginWithGoogleUnknown(int resultCode, @Nullable Intent data) {
        setPageState(new ErrorPageState(TAG, R.string.LOGIN_login_error, "Unknown result code: "+resultCode));
    }


    // Внутренние методы
    private void setLoggedInPageState() {
        setPageState(new LoggedInPageState());
    }

    private void setLoggedOutPageState() {
        setPageState(new LoggedOutPageState());
    }

    private void loginWithGoogleAccount(@NonNull GoogleSignInAccount googleSignInAccount) {

        setPageState(new ProgressPageState(R.string.LOGIN_logging_in));

        AuthSingleton.loginWithGoogle(googleSignInAccount, new iAuthSingleton.LoginCallbacks() {
            @Override
            public void onLoginSuccess(String userId) {

                mUsersSingleton.refreshUserFromServer(userId, new iUsersSingleton.iRefreshCallbacks() {
                    @Override
                    public void onUserRefreshSuccess(@NonNull User user) {
                        mUsersSingleton.storeCurrentUser(user);
                        finishLoginWithSuccess();
                    }

                    @Override
                    public void onUserNotExists() {
                        createNewUserFromGoogleAccount(googleSignInAccount);
                    }

                    @Override
                    public void onUserRefreshFail(String errorMsg) {
                        setPageState(new LoginErrorPageState(TAG, R.string.LOGIN_error_loading_user_info, errorMsg));
                    }
                });
            }

            @Override
            public void onLoginError(String errorMsg) {
                setPageState(new LoginErrorPageState(TAG, R.string.LOGIN_login_error, errorMsg));
            }

            @Override
            public void onWrongCredentialsError() {
                setPageState(new LoginErrorPageState(TAG, R.string.LOGIN_bad_credentials, ""));
            }

            @Override
            public void onTooManyLoginAttempts() {
                setPageState(new LoginErrorPageState(TAG, R.string.LOGIN_too_many_login_attempts, ""));
            }
        });
    }

    private void createNewUserFromGoogleAccount(@NonNull GoogleSignInAccount googleSignInAccount) {

        setPageState(new ProgressPageState(R.string.LOGIN_creating_new_user));

        User user = new User();
        user.setKey(AuthSingleton.currentUserId());
        user.setName(googleSignInAccount.getDisplayName());
        user.setEmail(googleSignInAccount.getEmail());
        user.setEmailVerified(true);
        user.setIsExternal(true);

        mUsersSingleton.createUser(user, new iUsersSingleton.iCreateCallbacks() {
            @Override
            public void onUserCreateSuccess(User user) {
                finishLoginWithSuccess();
            }

            @Override
            public void onUserCreateFail(String errorMsg) {
                setPageState(new LoginErrorPageState(TAG, R.string.LOGIN_error_creating_user, errorMsg));
            }
        });
    }

    private void finishLoginWithSuccess() {
        risePageEvent(new LoginSuccessPageEvent());
    }

}
