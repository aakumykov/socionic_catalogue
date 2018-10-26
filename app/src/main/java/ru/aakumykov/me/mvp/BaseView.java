package ru.aakumykov.me.mvp;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.BindView;
import ru.aakumykov.me.mvp.interfaces.iAuthService;
import ru.aakumykov.me.mvp.interfaces.iCardsService;
import ru.aakumykov.me.mvp.services.AuthService;
import ru.aakumykov.me.mvp.services.CardsService;


public abstract class BaseView extends AppCompatActivity implements
    iBaseView
{

    @BindView(R.id.messageView) TextView messageView;
    @BindView(R.id.progressBar) ProgressBar progressBar;

    private final static String TAG = "BaseView";

    private Intent cardsServiceIntent;
    private Intent authServiceIntent;

    private ServiceConnection cardsServiceConnection;
    private ServiceConnection authServiceConnection;

    private iCardsService cardsService;
    private boolean isCardsServiceBounded = false;

    private iAuthService authService;
    private boolean isAuthServiceBounded = false;

    public abstract void onServiceBounded();
    public abstract void onServiceUnbounded();


    // Системные методы
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Соединение со службой карточек
        cardsServiceIntent = new Intent(this, CardsService.class);

        cardsServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
//                Log.d(TAG, "onCardsServiceConnected()");

                CardsService.LocalBinder localBinder = (CardsService.LocalBinder) service;
                cardsService = localBinder.getService();
                isCardsServiceBounded = true;

                onServiceBounded();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
//                Log.d(TAG, "onCardsServiceDisconnected()");
                onServiceUnbounded();
                isCardsServiceBounded = false;
            }
        };

        // Соединение со службой авторизации
        authServiceIntent = new Intent(this, AuthService.class);

        authServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
//                Log.d(TAG, "onAuthServiceConnected()");

                AuthService.LocalBinder localBinder = (AuthService.LocalBinder) service;
                authService = localBinder.getService();
                isAuthServiceBounded = true;

                onServiceBounded();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
//                Log.d(TAG, "onAuthServiceDisconnected()");
                onServiceUnbounded();
                isAuthServiceBounded = false;
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindService(cardsServiceIntent, cardsServiceConnection, BIND_AUTO_CREATE);
        bindService(authServiceIntent, authServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isCardsServiceBounded)
            unbindService(cardsServiceConnection);
            unbindService(authServiceConnection);
    }


    // Сообщения пользователю
    @Override
    public void showInfoMsg(int messageId) {
        showMsg(getResources().getString(messageId), getResources().getColor(R.color.info));
    }

    @Override
    public void showInfoMsg(int userMessageId, String consoleMessage) {
        showInfoMsg(userMessageId);
        Log.d(TAG, consoleMessage);
    }

    @Override
    public void showErrorMsg(int messageId) {
        String msg = getResources().getString(messageId);
        showErrorMsg(msg);
        Log.e(TAG, msg);
    }

    @Override
    public void showErrorMsg(String message) {
        showMsg(message, getResources().getColor(R.color.error));
    }

    @Override
    public void showErrorMsg(int userMessageId, String consoleMessage) {
        showErrorMsg(userMessageId);
        Log.e(TAG, consoleMessage);
    }

    private void showMsg(String text, int color) {
        messageView.setText(text);
        messageView.setTextColor(color);
        MyUtils.show(messageView);
    }

    @Override
    public void hideMsg() {
        MyUtils.hide(messageView);
    }

    @Override
    public void showProgressBar() {
        MyUtils.show(progressBar);
    }

    @Override
    public void hideProgressBar() {
        MyUtils.hide(progressBar);
    }


    // Геттеры
    public iCardsService getCardsService() {
        return cardsService;
    }

    public iAuthService getAuthService() {
        return authService;
    }


    // Разное
    @Override
    public void closePage() {
        Log.d(TAG, "closePage()");
        finish();
    }
}
