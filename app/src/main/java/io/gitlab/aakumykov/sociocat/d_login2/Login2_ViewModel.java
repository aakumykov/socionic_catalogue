package io.gitlab.aakumykov.sociocat.d_login2;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import io.gitlab.aakumykov.sociocat.R;
import io.gitlab.aakumykov.sociocat.a_basic_mvvm_page_components.BasicMVVMPage_ViewModel;
import io.gitlab.aakumykov.sociocat.a_basic_mvvm_page_components.page_event.ToastPageEvent;
import io.gitlab.aakumykov.sociocat.a_basic_mvvm_page_components.page_state.ErrorPageState;
import io.gitlab.aakumykov.sociocat.a_basic_mvvm_page_components.page_state.NeutralPageState;
import io.gitlab.aakumykov.sociocat.a_basic_mvvm_page_components.page_state.ProgressPageState;
import io.gitlab.aakumykov.sociocat.d_login2.page_events.AlreadyLoggedInEvent;
import io.gitlab.aakumykov.sociocat.d_login2.page_events.LoginSuccessPageEvent;
import io.gitlab.aakumykov.sociocat.d_login2.page_events.LoginWithEmailAndPasswordEvent;
import io.gitlab.aakumykov.sociocat.d_login2.page_events.LoginWithGoogleEvent;
import io.gitlab.aakumykov.sociocat.d_login2.page_events.LoginWithVKEvent;
import io.gitlab.aakumykov.sociocat.singletons.AuthSingleton;
import io.gitlab.aakumykov.sociocat.singletons.UsersSingleton;
import io.gitlab.aakumykov.sociocat.singletons.iAuthSingleton;
import io.gitlab.aakumykov.sociocat.singletons.iUsersSingleton;
import io.gitlab.aakumykov.sociocat.utils.auth.GoogleAuthHelper;

public class Login2_ViewModel extends BasicMVVMPage_ViewModel {

    private final static String TAG = Login2_ViewModel.class.getSimpleName();
    private iUsersSingleton mUsersSingleton = UsersSingleton.getInstance();

    @Override
    protected void onColdStart() {
        if (AuthSingleton.isLoggedIn())
            risePageEvent(new AlreadyLoggedInEvent());
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


    public void onLoginWithGoogleOk(@Nullable Intent data) {

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


    private void loginWithGoogleAccount(GoogleSignInAccount googleSignInAccount) {

        setPageState(new ProgressPageState(R.string.LOGIN_logging_in));

        AuthSingleton.loginWithGoogle(googleSignInAccount, new iAuthSingleton.LoginCallbacks() {
            @Override
            public void onLoginSuccess(String userId) {

                risePageEvent(new LoginSuccessPageEvent(googleSignInAccount.getDisplayName() + " вошёл через Google"));

                /*loadUserFromServer(userId, new iLoadUserCallbacks() {
                    @Override
                    public void onUserLoadSuccess(User user) {
                        view.finishLogin(false, mTransitIntent);
                    }

                    @Override
                    public void onUserNotExists() {
                        createUserFromGoogleAccount(userId, googleSignInAccount);
                    }

                    @Override
                    public void onUserLoadError(String errorMsg) {
                        onLoadUserFromServerError(errorMsg);
                    }
                });*/

            }

            @Override
            public void onLoginError(String errorMsg) {
                setPageState(new ErrorPageState(TAG, R.string.LOGIN_login_error, errorMsg));
            }

            @Override
            public void onWrongCredentialsError() {
                setPageState(new ErrorPageState(TAG, R.string.LOGIN_bad_credentials, ""));
            }

            @Override
            public void onTooManyLoginAttempts() {
                setPageState(new ErrorPageState(TAG, R.string.LOGIN_too_many_login_attempts, ""));
            }
        });
    }

    /*private void loadUserFromServer(@NonNull String userId, iLoadUserCallbacks callbacks) {

        mUsersSingleton.refreshUserFromServer(userId, new iUsersSingleton.iRefreshCallbacks() {
            @Override
            public void onUserRefreshSuccess(@NonNull User user) {
                mUsersSingleton.storeCurrentUser(user);
                callbacks.onUserLoadSuccess(user);
            }

            @Override
            public void onUserNotExists() {
                callbacks.onUserNotExists();
            }

            @Override
            public void onUserRefreshFail(String errorMsg) {
                callbacks.onUserLoadError(errorMsg);
            }
        });
    }*/
}
