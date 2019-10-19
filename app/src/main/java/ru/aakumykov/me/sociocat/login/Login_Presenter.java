package ru.aakumykov.me.sociocat.login;

import android.content.Intent;
import android.os.Bundle;
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

public class Login_Presenter implements
        iLogin.Presenter
{
    private final static String TAG = "Login_Presenter";
    private iLogin.View view;

    private String mIntentAction;
    private Intent mTransitIntent;
    private Bundle mTransitArguments;

    private iUsersSingleton usersSingleton = UsersSingleton.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private String externalAccessToken;
    private String externalUserId;
    private String userName;
    private String customToken;
    private String internalUserId;

    // Обязательные методы
    @Override
    public void linkView(iLogin.View view) {
        this.view = view;
    }
    @Override
    public void unlinkView() {
        this.view = new Login_View_Stub();
    }


    // Интерфейсные методы
    @Override
    public void processInputIntent(@Nullable Intent intent) {

        if (null == intent) {
            view.showErrorMsg(R.string.LOGIN_data_error, "Intent is NULL");
            return;
        }

        mIntentAction = intent.getAction() + "";

        if (intent.hasExtra(Intent.EXTRA_INTENT))
            mTransitIntent = intent.getParcelableExtra(Intent.EXTRA_INTENT);

        if (intent.hasExtra(Constants.TRANSIT_ARGUMENTS))
            mTransitArguments = intent.getBundleExtra(Constants.TRANSIT_ARGUMENTS);

        if (mIntentAction.equals(Constants.ACTION_TRY_NEW_PASSWORD)) {
            view.showToast(R.string.LOGIN_try_new_password);
        }
    }

    @Override
    public void doLogin(String email, String password) {

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        String userId = authResult.getUser().getUid();

                        try {
                            usersSingleton.refreshUserFromServer(userId, new iUsersSingleton.RefreshCallbacks() {
                                @Override
                                public void onUserRefreshSuccess(User user) {
                                    try {
                                        processSuccessfullLogin(user);
                                    }
                                    catch (Exception e) {
                                        view.showToast(R.string.LOGIN_login_error);
                                        cancelLogin();
                                        Log.e(TAG, e.getMessage());
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onUserRefreshFail(String errorMsg) {
                                    showLoginError(errorMsg);
                                }
                            });
                        }
                        catch (Exception e) {
                            Log.e(TAG, e.getMessage());
                            e.printStackTrace();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, e.getMessage());
                        e.printStackTrace();
                    }
                });
    }

    @Override
    public void cancelLogin() {
        firebaseAuth.signOut();
        view.finishLogin(true, mTransitIntent, mTransitArguments);
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

//        // Это вообще не его дело!
//        if (mIntentAction.equals(Constants.ACTION_CREATE)) {
//            view.goCreateCard();
//            return;
//        }

        view.finishLogin(false, mTransitIntent, mTransitArguments);
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

                        view.finishLogin(false, mTransitIntent, mTransitArguments);
                    }

                    @Override
                    public void onCreateOrUpdateExternalUser_Error(String errorMsg) {
                        view.hideProgressMessage();
                        view.showErrorMsg(R.string.LOGIN_error_updating_user, errorMsg);
                    }
                }
        );
    }
}
