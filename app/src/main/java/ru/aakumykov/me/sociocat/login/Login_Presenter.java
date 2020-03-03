package ru.aakumykov.me.sociocat.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.DeepLink_Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.models.User;
import ru.aakumykov.me.sociocat.other.VKInteractor;
import ru.aakumykov.me.sociocat.singletons.AuthSingleton;
import ru.aakumykov.me.sociocat.singletons.UsersSingleton;
import ru.aakumykov.me.sociocat.singletons.iAuthSingleton;
import ru.aakumykov.me.sociocat.singletons.iUsersSingleton;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class Login_Presenter implements
        iLogin.Presenter
{
    private final static String TAG = "Login_Presenter";
    private iLogin.View view;

    private String mIntentAction;
    private Intent mTransitIntent;

    private iUsersSingleton usersSingleton = UsersSingleton.getInstance();

    private String externalAccessToken;
    private String externalUserId;
    private String userName;
    private String customToken;
    private String internalUserId;

    private boolean isVirgin = true;

    private iLogin.ViewState currentViewState = iLogin.ViewState.INITIAL;
    private int currentMessageId = -1;
    private String currentMessageDetails = null;


    // Обязательные методы
    @Override
    public void linkView(iLogin.View view) {
        this.view = view;
    }

    @Override
    public void unlinkView() {
        this.view = new Login_View_Stub();
    }

    @Override
    public boolean isVirgin() {
        return isVirgin;
    }

    @Override
    public void processInputIntent(@Nullable Intent intent) {

        isVirgin = false;

        if (null == intent) {
            view.setState(iLogin.ViewState.ERROR, R.string.LOGIN_data_error, "Intent is NULL");
            return;
        }

        if (intent.hasExtra(Constants.TRANSIT_INTENT))
            mTransitIntent = intent.getParcelableExtra(Constants.TRANSIT_INTENT);

        String action = intent.getAction() + "";
        if (TextUtils.isEmpty(action)) {
            view.setState(iLogin.ViewState.ERROR, R.string.LOGIN_data_error, "There is no action in Intent");
            return;
        }

        switch (action) {
            case Constants.ACTION_LOGIN:
                break;

            case Constants.ACTION_LOGIN_WITH_NEW_PASSWORD:
                loginWithNewPassword();
                break;

            case Constants.ACTION_LOGIN_VIA_EMAIL:
                loginViaEmailLink(intent);
                break;

            case Constants.ACTION_TRY_NEW_PASSWORD:
                view.showToast(R.string.LOGIN_try_new_password);
                break;

            case Constants.ACTION_CONTINUE_REGISTRATION:
                continueRegistration(intent);
                break;

            default:
                view.setState(iLogin.ViewState.ERROR, R.string.LOGIN_data_error, "Unknown intent's action: "+action);
                break;
        }
    }

    @Override
    public void onConfigChanged() {
        view.setState(currentViewState, currentMessageId, currentMessageDetails);
    }

    @Override
    public void storeViewState(iLogin.ViewState state, int messageId, String messageDetails) {
        currentViewState = state;
        currentMessageId = messageId;
        currentMessageDetails = messageDetails;
    }

    @Override
    public void onFormIsValid() {
        loginWithEmailAndPassword();
    }

    @Override
    public void onLoginWithGoogleClicked() {
        view.startLoginWithGoogle();
    }

    @Override
    public void onGoogleLoginResult(@NonNull GoogleSignInAccount googleSignInAccount) {

        view.setState(iLogin.ViewState.PROGRESS, R.string.LOGIN_logging_in);

        AuthSingleton.loginWithGoogle(googleSignInAccount, new iAuthSingleton.LoginCallbacks() {
            @Override
            public void onLoginSuccess(String userId) {
                loadUserFromServer(userId, new iLoadUserCallbacks() {
                    @Override
                    public void onUserLoadSuccess(User user) {
                        Log.d(TAG, "User exists: "+user);
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
                });
            }

            @Override
            public void onLoginError(String errorMsg) {
                showError(R.string.LOGIN_login_error, errorMsg);
            }

            @Override
            public void onWrongCredentialsError() {
                showBadCredentialsError();
            }

            @Override
            public void onTooManyLoginAttempts() {
                showTooManyLoginAttemptsError();
            }
        });
    }

    @Override
    public void onLogoutFromGoogleClicked() {
        view.logoutFromGoogle();
    }

    @Override
    public void cancelLogin() {
        AuthSingleton.signOut();
        view.finishLogin(true, mTransitIntent);
    }

    @Override
    public void onVKLoginButtonClicked() {
        VKInteractor.login(view.getActivity());
    }

    @Override
    public void processVKLogin(int vk_user_id, String vk_access_token) {

        this.externalUserId = String.valueOf(vk_user_id);
        this.externalAccessToken = vk_access_token;

        view.disableForm();
        view.showProgressMessage(R.string.LOGIN_getting_user_info);

        VKInteractor.getUserInfo(vk_user_id, new VKInteractor.GetVKUserInfo_Callbacks() {
            @Override
            public void onGetVKUserInfoSuccess(VKInteractor.VKUser vkUser) {
                String firstName = vkUser.getFirstName();
                String lastName = vkUser.getLastName();

                Login_Presenter.this.userName =  firstName;
                if (!TextUtils.isEmpty(lastName))
                    Login_Presenter.this.userName += " " + lastName;

                createCustomToken();
            }

            @Override
            public void onGetVKUserInfoError(String errorMsg) {
                view.enableForm();
                view.showErrorMsg(R.string.LOGIN_error_loading_user_info, errorMsg);
            }
        });
    }


    // Внутренние методы
    private void loginWithEmailAndPassword() {

        String email = view.getEmail();
        String password = view.getPassword();

        view.setState(iLogin.ViewState.PROGRESS, R.string.LOGIN_logging_in);

        AuthSingleton.loginWithEmailAndPassword(email, password, new iAuthSingleton.LoginCallbacks() {
            @Override
            public void onLoginSuccess(String userId) {

                loadUserFromServer(userId, new iLoadUserCallbacks() {
                    @Override
                    public void onUserLoadSuccess(User user) {
                        view.setState(iLogin.ViewState.SUCCESS, R.string.LOGIN_login_success);
                        view.finishLogin(false, mTransitIntent);
                    }

                    @Override
                    public void onUserNotExists() {
                        showError(R.string.LOGIN_error_user_not_found, "User with email '"+email+"' not exists");
                    }

                    @Override
                    public void onUserLoadError(String errorMsg) {
                        onLoadUserFromServerError(errorMsg);
                    }
                });
            }

            @Override
            public void onWrongCredentialsError() {
                view.setState(iLogin.ViewState.ERROR, R.string.LOGIN_bad_credentials);
            }

            @Override
            public void onTooManyLoginAttempts() {
                showTooManyLoginAttemptsError();
            }

            @Override
            public void onLoginError(String errorMsg) {
                view.setState(iLogin.ViewState.ERROR, R.string.LOGIN_login_error, errorMsg);
            }
        });
    }

    private void loginViaEmailLink(@NonNull Intent intent) {

        String emailLoginLink = intent.getStringExtra(Intent.EXTRA_TEXT);

        if (null == emailLoginLink) {
            view.setState(iLogin.ViewState.ERROR, R.string.LOGIN_login_link_is_broken, "Eamil login link is null");
            return;
        }

        SharedPreferences sharedPreferences = view.getSharedPrefs(Constants.SHARED_PREFERENCES_USER);
        String storedEmail = sharedPreferences.getString(Constants.KEY_STORED_EMAIL, "");

        view.setState(iLogin.ViewState.PROGRESS, R.string.LOGIN_logging_in);

        AuthSingleton.loginWithEmailLink(storedEmail, emailLoginLink, new iAuthSingleton.EmailLinkSignInCallbacks() {

            @Override
            public void onLoginSuccess(String userId) {
                loadUserFromServer(userId, new iLoadUserCallbacks() {
                    @Override
                    public void onUserLoadSuccess(User user) {
                        continueLoginViaEmailAsExistingUser(emailLoginLink);
                    }

                    @Override
                    public void onUserNotExists() {
                        continueLoginViaEmailAsNewUser(emailLoginLink);
                    }

                    @Override
                    public void onUserLoadError(String errorMsg) {
                        onLoadUserFromServerError(errorMsg);
                    }
                });
            }

            @Override
            public void onWrongCredentialsError() {
                showBadCredentialsError();
            }

            @Override
            public void onTooManyLoginAttempts() {
                showTooManyLoginAttemptsError();
            }

            @Override
            public void onLoginError(String errorMsg) {
                showError(R.string.login_error, errorMsg);
                AuthSingleton.logout();
            }

            @Override
            public void onLoginLinkHasExpired() {
                showError(R.string.LOGIN_error_login_link_has_expired, null);
                AuthSingleton.logout();
            }
        });
    }

    private void loginWithNewPassword() {
        view.setState(iLogin.ViewState.INFO, R.string.LOGIN_try_new_password);
    }

    private void loadUserFromServer(@NonNull String userId, iLoadUserCallbacks callbacks) {

        usersSingleton.refreshUserFromServer(userId, new iUsersSingleton.RefreshCallbacks() {
            @Override
            public void onUserRefreshSuccess(User user) {
                usersSingleton.storeCurrentUser(user);
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
    }

    private void continueLoginViaEmailAsExistingUser(@NonNull String emailLoginLink) {
        Log.i(TAG, "continueAsExistingUser: "+emailLoginLink);
        view.setState(iLogin.ViewState.SUCCESS, R.string.LOGIN_login_success);
        view.finishLogin(false, mTransitIntent);
    }

    private void continueLoginViaEmailAsNewUser(@NonNull String emailLoginLink) {
        String userId = AuthSingleton.currentUserId();
        view.go2finishRegistration(userId);
    }

    private void continueRegistration(@NonNull Intent intent) {

//        view.setState(iLogin.ViewStates.PROGRESS, R.string._continuing_registration);


    }

    private void createUserFromGoogleAccount(@NonNull String userId, @NonNull GoogleSignInAccount googleSignInAccount) {
        Log.d(TAG, "googleSignInAccount: "+googleSignInAccount);

        String userName = googleSignInAccount.getDisplayName();
        String email = googleSignInAccount.getEmail();

        usersSingleton.createUser(userId, userName, email, new iUsersSingleton.CreateCallbacks() {
            @Override
            public void onUserCreateSuccess(User user) {
                usersSingleton.storeCurrentUser(user);
                view.finishLogin(false, mTransitIntent);
            }

            @Override
            public void onUserCreateFail(String errorMsg) {
                showError(R.string.LOGIN_login_error, errorMsg);
                view.logoutFromGoogle();
                AuthSingleton.logout();
            }
        });
    }

    private void processEmailLoginAction(@NonNull Intent intent) {

        String deepLink = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (null == deepLink) {
            showError(R.string.LOGIN_link_processing_error, "There is no deep link");
            return;
        }

        Log.d(TAG, "deepLink: "+deepLink);

        Uri deepLinkURI = Uri.parse(deepLink);

        String link = deepLinkURI.getQueryParameter(DeepLink_Constants.KEY_LINK);
        if (TextUtils.isEmpty(link)) {
            showError(R.string.LOGIN_link_processing_error, "There is no link parameter in deepLink");
            return;
        }

        Uri linkURI = Uri.parse(link);

        String continueURL = linkURI.getQueryParameter(DeepLink_Constants.KEY_CONTINUE_URL);
        if (TextUtils.isEmpty(continueURL)) {
            showError(R.string.LOGIN_link_processing_error, "There is no continueUrl in link");
            return;
        }

        Uri continueURI = Uri.parse(continueURL);

        String action = continueURI.getQueryParameter(DeepLink_Constants.KEY_ACTION);
        if (TextUtils.isEmpty(action)) {
            showError(R.string.LOGIN_link_processing_error, "There is no action in continueUrl");
            return;
        }

        switch (action) {
            case DeepLink_Constants.ACTION_CHANGE_EMAIL:
                changeEmail(continueURI);
                break;

            default:
                showError(R.string.LOGIN_link_processing_error, "Unknown action: "+action);
        }
    }

    private void changeEmail(Uri continueURI) {

        String email = continueURI.getQueryParameter(DeepLink_Constants.KEY_EMAIL);
        String userIdFromURI = continueURI.getQueryParameter(DeepLink_Constants.KEY_USER_ID);

        String currentUserId = usersSingleton.getCurrentUser().getKey();

        Log.d(TAG, "email: "+email+", userIdFromURI: "+userIdFromURI+", currentUserId: "+currentUserId);
    }

    private void createCustomToken() {

        view.showProgressMessage(R.string.LOGIN_creating_custom_token);

        AuthSingleton.createFirebaseCustomToken(externalUserId, new iAuthSingleton.CreateFirebaseCustomToken_Callbacks() {
            @Override
            public void onCreateFirebaseCustomToken_Success(String customToken) {
                Login_Presenter.this.customToken = customToken;
                loginExternalUserToFirebase();
            }

            @Override
            public void onCreateFirebaseCustomToken_Error(String errorMsg) {
                view.showErrorMsg(R.string.LOGIN_error_creating_custom_token, errorMsg);
            }
        });
    }

    private void loginExternalUserToFirebase() {

        view.showProgressMessage(R.string.LOGIN_logging_in);

        FirebaseAuth
                .getInstance()
                .signInWithCustomToken(customToken)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        FirebaseUser firebaseUser = authResult.getUser();
                        String firebaseUserId = firebaseUser.getUid();

                        createOrUpdateUser(firebaseUserId, externalUserId, userName);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        AuthSingleton.logout();
                        view.showErrorMsg(R.string.LOGIN_login_error, e.getMessage());
                        e.printStackTrace();
                    }
                });

    }

    private void createOrUpdateUser(String internalUserId, String externalUserId, String userName) {
        view.showProgressMessage(R.string.LOGIN_updating_user);

        try {
            usersSingleton.createOrUpdateExternalUser(
                    internalUserId,
                    externalUserId,
                    userName,
                    new iUsersSingleton.CreateOrUpdateExternalUser_Callbacks() {
                        @Override
                        public void onCreateOrUpdateExternalUser_Success(User user) {
                            view.hideProgressMessage();
                            view.showToast(R.string.LOGIN_login_success);

                            try {
                                usersSingleton.storeCurrentUser(user);
                            } catch (Exception e) {
                                Log.e(TAG, e.getMessage());
                                e.printStackTrace();
                            }

                            view.finishLogin(false, mTransitIntent);
                        }

                        @Override
                        public void onCreateOrUpdateExternalUser_Error(String errorMsg) {
                            view.hideProgressMessage();
                            view.showErrorMsg(R.string.LOGIN_error_updating_user, errorMsg);
                        }
                    }
            );
        }
        catch (UsersSingleton.UsersSingletonException e) {
            view.hideProgressMessage();
            view.showErrorMsg(R.string.LOGIN_error_updating_user, e.getMessage());
            MyUtils.printError(TAG, e);
        }
    }

    private void onLoadUserFromServerError(String errorMsg) {
        AuthSingleton.logout();
        showError(R.string.LOGIN_error_loading_user_info, errorMsg);
    }

    private void showError(int messageId, @Nullable String messageDetails) {
        view.setState(iLogin.ViewState.ERROR, messageId, messageDetails);
        if (null != messageDetails)
            Log.e(TAG, messageDetails);
    }


    private void showBadCredentialsError() {
        showError(R.string.LOGIN_bad_credentials, null);
    }

    private void showTooManyLoginAttemptsError() {
        showError(R.string.LOGIN_too_many_login_attempts, null);
    }


    // Внутренние интерфейсы
    private interface iLoadUserCallbacks {
        void onUserLoadSuccess(User user);
        void onUserNotExists();
        void onUserLoadError(String errorMsg);
    }
}
