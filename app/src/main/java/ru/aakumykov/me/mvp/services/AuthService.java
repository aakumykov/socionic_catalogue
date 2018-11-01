package ru.aakumykov.me.mvp.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AdditionalUserInfo;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;

import ru.aakumykov.me.mvp.interfaces.iAuthService;

public class AuthService extends Service implements
        iAuthService
{
    // Внутренний класс
    public class LocalBinder extends Binder {
        public AuthService getService() {
            return AuthService.this;
        }
    }

    // Свойства
    private final static String TAG = "AuthService";
    private final IBinder binder;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();


    // Системные методы
    public AuthService() {
//        Log.d(TAG, "new AuthService()");
        binder = new LocalBinder();
    }

    @Override
    public IBinder onBind(Intent intent) {
//        Log.d(TAG, "onBind()");
        return binder;
    }


    // Интерфейсные методы
    @Override
    public boolean isAuthorized() {
        return null != firebaseAuth.getCurrentUser();
    }

    @Override
    public boolean isAdmin() {
        return false;
    }

    @Override
    public void registerWithEmail(
            String email,
            String password,
            final iAuthService.RegisterCallbacks callbacks
    ) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                callbacks.onRegSucsess(authResult.getUser().getUid());
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
    public void createUser(String uid, String name, CreateUserCallbacks callbacks) {

    }
}

