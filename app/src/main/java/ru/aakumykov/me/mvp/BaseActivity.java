package ru.aakumykov.me.mvp;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import butterknife.BindView;
import ru.aakumykov.me.mvp.interfaces.iCardsService;
import ru.aakumykov.me.mvp.services.CardsService;


public abstract class BaseActivity extends AppCompatActivity {

    @BindView(R.id.messageView) TextView messageView;

    private final static String TAG = "BaseActivity";
    private Intent cardsServiceIntent;
    private ServiceConnection cardsServiceConnection;
    private iCardsService cardsService;
    private boolean isCardsServiceBounded = false;

    public abstract void onServiceBounded();
    public abstract void onServiceUnbounded();


    // Системные методы
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Соединение со службой
        cardsServiceIntent = new Intent(this, CardsService.class);

        cardsServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d(TAG, "onServiceConnected()");

                CardsService.LocalBinder localBinder = (CardsService.LocalBinder) service;
                cardsService = localBinder.getService();
                isCardsServiceBounded = true;

                onServiceBounded();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d(TAG, "onServiceDisconnected()");

                isCardsServiceBounded = false;

                onServiceUnbounded();
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindService(cardsServiceIntent, cardsServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isCardsServiceBounded)
            unbindService(cardsServiceConnection);
    }

    // Сообщения пользователю
    public void showInfoMsg(int messageId) {
        showMsg(getResources().getString(messageId), getResources().getColor(R.color.info));
    }

    public void showErrorMsg(int messageId) {
        String msg = getResources().getString(messageId);
        showErrorMsg(msg);
        Log.e(TAG, msg);
    }

    public void showErrorMsg(String message) {
        showMsg(message, getResources().getColor(R.color.error));
    }

    private void showMsg(String text, int color) {
        messageView.setText(text);
        messageView.setTextColor(color);
        MyUtils.show(messageView);
    }

    public void hideMsg() {
        MyUtils.hide(messageView);
    }

    // Геттеры
    public iCardsService getCardsService() {
        return cardsService;
    }

}
