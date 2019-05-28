package ru.aakumykov.me.sociocat.login;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.singletons.iUsersSingleton;
import ru.aakumykov.me.sociocat.models.User;
import ru.aakumykov.me.sociocat.singletons.UsersSingleton;

public class Login_Presenter implements
        iLogin.Presenter
{
    //private final static String TAG = "Login_Presenter";
    private iLogin.View view;

    private String intentAction;
    private Intent originalIntent;

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

        String action = intent.getAction();
        this.intentAction = action;
        this.originalIntent = intent;

        if (Constants.ACTION_TRY_NEW_PASSWORD.equals(action)) {
            view.showToast(R.string.LOGIN_try_new_password);
        }

        /*switch (action) {
            case Constants.ACTION_TRY_NEW_PASSWORD:
                view.showToast(R.string.LOGIN_try_new_password);
                break;
            default:
                view.showErrorMsg(R.string.LOGIN_data_error, "Unknown action: "+action);
        }*/
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
                                postLoginProcess(user);
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
        view.finishLogin(true);
    }


    // Внутренние методы
    private void postLoginProcess(User user) {

        if (!user.isEmailVerified()) {
            view.notifyToConfirmEmail(user.getKey());
            return;
        }

        if (intentAction.equals(Constants.ACTION_CREATE)) {
            view.goCreateCard();
            return;
        }

        /*if (intentAction.equals(Constants.ACTION_LOGIN_REQUEST)) {
            view.proceedLoginRequest(originalIntent);
            return;
        }*/

        view.finishLogin(false);
    }

    private void showLoginError(String msg) {
        view.hideProgressMessage();
        view.enableForm();
        view.showErrorMsg(R.string.LOGIN_login_error, msg);
    }
}
