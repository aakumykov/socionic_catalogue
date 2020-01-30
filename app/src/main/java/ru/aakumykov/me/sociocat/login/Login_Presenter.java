package ru.aakumykov.me.sociocat.login;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ru.aakumykov.me.sociocat.Constants;
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
    public void onLoginClicked() {
        String email = view.getEmail();
        String password = view.getPassword();

        view.setState(iLogin.ViewState.PROGRESS, R.string.LOGIN_logging_in);

        AuthSingleton.loginWithEmailAndPassword(email, password, new iAuthSingleton.LoginCallbacks() {
            @Override
            public void onLoginSuccess(String userId) {
                view.setState(iLogin.ViewState.SUCCESS, R.string.LOGIN_login_success);
                refreshUserFromServer(userId);
            }

            @Override
            public void onLoginError(String errorMsg) {
                view.setState(iLogin.ViewState.ERROR, R.string.LOGIN_login_error, errorMsg);
            }
        });
    }


    // Интерфейсные методы
    @Override
    public void processInputIntent(@Nullable Intent intent) {

        isVirgin = false;

        if (null == intent) {
            view.showErrorMsg(R.string.LOGIN_data_error, "Intent is NULL");
            return;
        }

        mIntentAction = intent.getAction() + "";

        if (intent.hasExtra(Constants.TRANSIT_INTENT))
            mTransitIntent = intent.getParcelableExtra(Constants.TRANSIT_INTENT);

        if (mIntentAction.equals(Constants.ACTION_TRY_NEW_PASSWORD)) {
            view.showToast(R.string.LOGIN_try_new_password);
        }
    }

    @Override
    public void doLogin(String email, String password) {


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
                view.showErrorMsg(R.string.LOGIN_error_getting_user_info, errorMsg);
            }
        });
    }


    // Внутренние методы
    private void processSuccessfullLogin(User user) {
        if (!user.isEmailVerified()) {
            view.notifyToConfirmEmail(user.getKey());
            return;
        }

        view.finishLogin(false, mTransitIntent);
    }

    private void showLoginError(String msg) {
        view.hideProgressMessage();
        view.enableForm();
        view.showErrorMsg(R.string.LOGIN_login_error, msg);
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

    private void refreshUserFromServer(@NonNull String userId) {

        try {
            usersSingleton.refreshUserFromServer(userId, new iUsersSingleton.RefreshCallbacks() {
                @Override
                public void onUserRefreshSuccess(User user) {
                    usersSingleton.storeCurrentUser(user);
                }

                @Override
                public void onUserRefreshFail(String errorMsg) {
                    onUserRefreshError(errorMsg);
                }
            });
        }
        catch (Exception e) {
            onUserRefreshError(e.getMessage());
            MyUtils.printError(TAG, e);
        }
    }

    private void onUserRefreshError(String errorMsg) {
        view.setState(iLogin.ViewState.ERROR, R.string.LOGIN_error_getting_user_info, errorMsg);
        AuthSingleton.logout();
    }
}
