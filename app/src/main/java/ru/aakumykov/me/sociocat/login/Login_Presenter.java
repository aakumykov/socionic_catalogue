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

        FirebaseAuth
                .getInstance()
                .signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> fetchUser(authResult.getUser().getUid()))
                .addOnFailureListener(e -> {
                    showLoginError(e.getMessage());
                    e.printStackTrace();
                });
    }

    @Override
    public void cancelLogin() {
        firebaseAuth.signOut();
        view.finishLogin(true);
    }


    // Внутренние методы
    private void fetchUser(String userId) {
        usersSingleton.refreshUserFromServer(userId, new iUsersSingleton.ReadCallbacks() {
            @Override
            public void onUserReadSuccess(User user) {
                fetchAdminsList();
            }

            @Override
            public void onUserReadFail(String errorMsg) {
                showLoginError(errorMsg);
            }
        });
    }

    private void fetchAdminsList() {

        FirebaseDatabase
                .getInstance()
                .getReference()
                .child("/admins")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        HashMap<String,Boolean> list = new HashMap<>();
                        for (DataSnapshot snapshotItem : dataSnapshot.getChildren())
                            list.put(snapshotItem.getKey(), true);

                        usersSingleton.storeAdminsList(list);

                        postLoginProcess(usersSingleton.getCurrentUser());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        showLoginError(databaseError.getMessage());
                        databaseError.toException().printStackTrace();
                    }
                });
    }

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
