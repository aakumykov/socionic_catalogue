package ru.aakumykov.me.sociocat.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import ru.aakumykov.me.sociocat.event_bus_objects.NewCardEvent;

public class NewCardsService extends Service {

    private static final String TAG = "NewCardsService";
    private int newCardsCount = 0;
    private ServiceBinder binder = new ServiceBinder();


    // Service
    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate()");
        super.onCreate();
        EventBus.getDefault().register(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand()");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind()");
        return binder;
    }


    // Подписка на событие NewCardEvent
    @Subscribe
    public void onNewCardEvent(NewCardEvent newCardEvent) {
        Log.d(TAG, "onNewCardEvent()");
        incrementNewCardsCount();
    }


    // Внешние методы
    public int getNewCardsCount() {
        return newCardsCount;
    }

    public void resetNewCardsCount() {
        setNewCardsCount(0);
    }


    // Внутренние методы
    private void incrementNewCardsCount() {
        setNewCardsCount(newCardsCount+1);
    }

    private synchronized void setNewCardsCount(int count) {
        newCardsCount = count;
    }


    public class ServiceBinder extends Binder {
        public NewCardsService getService() {
            return NewCardsService.this;
        }
    }
}
