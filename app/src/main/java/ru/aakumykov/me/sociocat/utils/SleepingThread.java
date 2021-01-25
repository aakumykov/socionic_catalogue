package ru.aakumykov.me.sociocat.utils;

import androidx.annotation.NonNull;

import java.util.concurrent.TimeUnit;

public class SleepingThread extends Thread {

    private int mTimeout;
    private iSleepingThreadCallbacks callbacks;

    public SleepingThread(
            int timeToSleep,
            iSleepingThreadCallbacks DSTCallbacks
    ) {
        mTimeout = timeToSleep;
        callbacks = DSTCallbacks;
    }

    @Override
    public synchronized void start() {
        super.start(); // ?

        new Thread(new Runnable() {
            @Override
            public void run() {
                callbacks.onSleepingStart();

                while (mTimeout >= 0 && !callbacks.isReadyToWakeUpNow()) {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    callbacks.onSleepingTick(mTimeout);

                    mTimeout--;
                }

                callbacks.onSleepingEnd();
            }
        }).start();
    }

    public interface iSleepingThreadCallbacks {
        void onSleepingStart();
        void onSleepingTick(int secondsToWakeUp);
        void onSleepingEnd();
        boolean isReadyToWakeUpNow();
        void onSleepingError(@NonNull String errorMsg);
    }
}

