package ru.aakumykov.me.mvp.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

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
    private final static String TAG = "CardsService";
    private final IBinder binder;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();


    // Системные методы
    public AuthService() {
        Log.d(TAG, "new AuthService()");
        binder = new LocalBinder();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind()");
        return binder;
    }


    // Пользовательские методы

    @Override
    public boolean isAuthorized() {
        return null != firebaseAuth.getCurrentUser();
    }

    @Override
    public boolean isAdmin() {
        return false;
    }
}
