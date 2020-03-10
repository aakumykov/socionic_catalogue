package ru.aakumykov.me.sociocat.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class NewCardsService extends Service {

    private static final String TAG = "NewCardsService";
    private int newCardsCount = 0;

    public NewCardsService() {
    }

    // Service
    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate()");
        super.onCreate();
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
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind()");
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
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
}
