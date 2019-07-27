package ru.aakumykov.me.sociocat.login;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.TimeUnit;

import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.other.VKInteractor;
import ru.aakumykov.me.sociocat.singletons.AuthSingleton;
import ru.aakumykov.me.sociocat.singletons.iAuthSingleton;
import ru.aakumykov.me.sociocat.singletons.iUsersSingleton;
import ru.aakumykov.me.sociocat.models.User;
import ru.aakumykov.me.sociocat.singletons.UsersSingleton;

public class Login_Presenter implements
        iLogin.Presenter
{
    //private final static String TAG = "Login_Presenter";
    private iLogin.View view;

    private String mIntentAction;
    private Intent mTransitIntent;
    private Bundle mTransitArguments;

    private iUsersSingleton usersSingleton = UsersSingleton.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();


    // Обязательные методы
    @Override
    public void linkView(iLogin.View view) {
        this.view = view;
    }
    @Override
    public void unlinkView() {
        this.view = null;
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

                        usersSingleton.refreshUserFromServer(userId, new iUsersSingleton.RefreshCallbacks() {
                            @Override
                            public void onUserRefreshSuccess(User user) {
                                processSuccessfullLogin(user);
                            }

                            @Override
                            public void onUserRefreshFail(String errorMsg) {
                                showLoginError(errorMsg);
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

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

    public void processVKLogin(int vm_user_id, String vk_access_token) {

        view.disableForm();
        view.showProgressMessage(R.string.LOGIN_creating_custom_token);

        String externalToken = String.valueOf(vm_user_id);
        AuthSingleton.createFirebaseCustomToken(externalToken, new iAuthSingleton.CreateFirebaseCustomToken_Callbacks() {
            @Override
            public void onCreateFirebaseCustomToken_Success(String customToken) {
                view.enableForm();
                view.showDebugMsg(customToken);

                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {

                }

                view.showProgressMessage(R.string.LOGIN_logging_in);

                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

                firebaseAuth.signInWithCustomToken(customToken)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                                if (null != firebaseUser) {
                                    view.showDebugMsg("Успешный вход через Firebase");
//                                    firebaseUser.updateProfile()
//                                    String userName = firebaseUser.getDisplayName();
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                view.showErrorMsg(R.string.LOGIN_error_login_via_vkontakte, e.getMessage());
                                e.printStackTrace();
                            }
                        });
            }

            @Override
            public void onCreateFirebaseCustomToken_Error(String errorMsg) {
                view.enableForm();
                view.showErrorMsg(R.string.LOGIN_error_login_via_vkontakte, errorMsg);
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

}
