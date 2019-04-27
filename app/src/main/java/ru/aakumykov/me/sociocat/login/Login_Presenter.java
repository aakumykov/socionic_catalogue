package ru.aakumykov.me.sociocat.login;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.interfaces.iUsersSingleton;
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

        if (null != intent) {

            String action = intent.getAction() + "";
            this.intentAction = action;
            this.originalIntent = intent;

            if (Constants.ACTION_TRY_NEW_PASSWORD.equals(action)) {
                view.showInfoMsg(R.string.LOGIN_try_new_password);
            }
        }
    }

    @Override
    public void doLogin(String email, String password) {
        usersSingleton.refreshUserFromServer(new iUsersSingleton.RefreshCallbacks() {
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

        if (intentAction.equals(Constants.ACTION_LOGIN_REQUEST)) {
            view.proceedLoginRequest(originalIntent);
            return;
        }

        view.finishLogin(false);
    }

    private void showLoginError(String msg) {
        view.hideProgressBar();
        view.enableForm();
        view.showErrorMsg(R.string.LOGIN_login_error, msg);
    }
}
