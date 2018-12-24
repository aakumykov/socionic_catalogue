package ru.aakumykov.me.mvp.services;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.interfaces.iAuthSingleton;
import ru.aakumykov.me.mvp.interfaces.iUsersSingleton;
import ru.aakumykov.me.mvp.models.Card;
import ru.aakumykov.me.mvp.models.User;

// TODO: разобраться с гостевым пользователем

public class AuthSingleton implements iAuthSingleton
{
    /* Одиночка */
    private static volatile AuthSingleton ourInstance;
    public synchronized static AuthSingleton getInstance() {
        synchronized (AuthSingleton.class) {
            if (null == ourInstance) ourInstance = new AuthSingleton();
            return ourInstance;
        }
    }
    private AuthSingleton() {
        firebaseAuth = FirebaseAuth.getInstance();
        usersService = UsersSingleton.getInstance();
    }
    /* Одиночка */    


    // Свойства
    private final static String TAG = "AuthSingleton";
    private FirebaseAuth firebaseAuth;
    private iUsersSingleton usersService;
    private User currentUser;


    // Интерфейсные методы

    // Регистрация, вход, выход
    @Override
    public void registerWithEmail(String email, String password,
            final iAuthSingleton.RegisterCallbacks callbacks) throws Exception
    {
        Log.d(TAG, "registerWithEmail("+email+", ***)");

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        callbacks.onRegSucsess(authResult.getUser().getUid(), authResult.getUser().getEmail());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callbacks.onRegFail(e.getMessage());
                        e.printStackTrace();
                    }
                });
    }

    @Override
    public void login(String email, String password, final LoginCallbacks callbacks) throws Exception {

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        callbacks.onLoginSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callbacks.onLoginFail(e.getMessage());
                        e.printStackTrace();
                    }
                });
    }

    @Override
    public void cancelLogin() {
        firebaseAuth.signOut();
    }

    @Override
    public void logout() {
        firebaseAuth.signOut();
    }


    @Override
    public void restoreCurrentUser(final iAuthSingleton.UserRestoreCallbacks callbacks) {
        usersService.getUser(currentUserId(), new iUsersSingleton.ReadCallbacks() {
            @Override
            public void onUserReadSuccess(User user) {
                storeCurrentUser(user);
                callbacks.onUserRestoreSuccess();
            }

            @Override
            public void onUserReadFail(String errorMsg) {
                callbacks.onUserRestoreFail(errorMsg);
            }
        });
    }


    // Параметры текущего пользователя
    @Override
    public User currentUser() {
        return this.currentUser;
    }

    @Override
    public String currentUserId() /*throws Exception*/ {
        String firebaseUid = firebaseAuth.getUid();
//        String userId = getCurrentUser().getKey();
//        if (!firebaseUid.equals(userId)) throw new Exception("Firebase user id != program user uid");
        return firebaseUid;
    }

    @Override
    public String currentUserName() {
        return currentUser.getName();
    }

    @Override
    public boolean isUserLoggedIn() {
        return null != firebaseAuth.getCurrentUser();
    }

    @Override
    public boolean isAdmin() {
        return false;
    }

    @Override
    public boolean userIsAdmin(String userId) {
        return false;
    }

    @Override
    public boolean isCardOwner(Card card) {
        return card.getUserId().equals(currentUserId());
    }

    // Служебные
    @Override
    public void storeCurrentUser(final User user) {
        this.currentUser = user;
    }

    @Override
    public void clearCurrentUser() {
        this.currentUser = null;
    }


    // Внутренние
    private User getCurrentUser() {
        return this.currentUser;
    }
}

