package ru.aakumykov.me.sociocat.login;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.interfaces.iAuthSingleton;
import ru.aakumykov.me.sociocat.interfaces.iUsersSingleton;
import ru.aakumykov.me.sociocat.models.User;
import ru.aakumykov.me.sociocat.singletons.AuthSingleton;
import ru.aakumykov.me.sociocat.singletons.UsersSingleton;

public class Login_Presenter implements
        iLogin.Presenter,
        iAuthSingleton.LoginCallbacks
{
    private final static String TAG = "Login_Presenter";
    private iLogin.View view;
    private iAuthSingleton authSingleton = AuthSingleton.getInstance();
    private iUsersSingleton usersSingleton = UsersSingleton.getInstance();
    private String intentAction;
    private Intent originalIntent;
    private String currentUserId;

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

            switch (action) {
                case Constants.ACTION_TRY_NEW_PASSWORD:
                    view.showInfoMsg(R.string.LOGIN_try_new_password);
                    break;

                default:
                    break;
            }
        }
    }

    @Override
    public void doLogin(String email, String password) {
        try {
            authSingleton.login(email, password, this);
        }
        catch (Exception e) {
            view.hideProgressBar();
            view.enableForm();
            view.showErrorMsg(R.string.LOGIN_login_failed, e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void cancelLogin() {
        authSingleton.cancelLogin();
        view.finishLogin(true);
    }


    // Методы обратного вызова
    @Override
    public void onLoginSuccess(final String userId) {
        currentUserId = userId;
        readAdminsList();
    }

    @Override
    public void onLoginFail(String errorMsg) {
        view.hideProgressBar();
        view.enableForm();
        view.showErrorMsg(errorMsg);
    }


    // Внутренние методы
    private void readAdminsList() {
        FirebaseDatabase
                .getInstance()
                .getReference()
                .child("/admins")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        HashMap<String,Boolean> list = new HashMap<>();
                        for (DataSnapshot snapshotItem : dataSnapshot.getChildren()) {
                            list.put(snapshotItem.getKey(), true);
                        }
                        usersSingleton.storeAdminsList(list);
                        fetchUserData();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        view.showErrorMsg(R.string.LOGIN_login_error, databaseError.getMessage());
                        databaseError.toException().printStackTrace();
                    }
                });
    }

    private void fetchUserData() {

        // TODO: в MyApp также запрашиваются даные пользователя с сервера!

        usersSingleton.getUserById(currentUserId, new iUsersSingleton.ReadCallbacks() {
            @Override
            public void onUserReadSuccess(User user) {

                if (!user.isEmailVerified()) {
                    view.notifyToConfirmEmail(currentUserId);
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

                view.hideProgressBar();
                view.finishLogin(false);
            }

            @Override
            public void onUserReadFail(String errorMsg) {
                view.showToast(R.string.LOGIN_login_error);
                Log.e(TAG, errorMsg);
                authSingleton.logout();
            }
        });
    }
}
