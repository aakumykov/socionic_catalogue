package ru.aakumykov.me.sociocat.z_rules_test;

import androidx.annotation.NonNull;

import java.util.concurrent.TimeUnit;

public class DoubleSleepingThread extends Thread {

    private int mTimeout;
    private iDSTCallbacks mDSTCallbacks;

    public DoubleSleepingThread(
            int timeToSleep,
            iDSTCallbacks DSTCallbacks
    ) {
        mTimeout = timeToSleep;
        mDSTCallbacks = DSTCallbacks;
    }

    @Override
    public synchronized void start() {
        super.start(); // ?

        new Thread(new Runnable() {
            @Override
            public void run() {
                mDSTCallbacks.onSleepingStart();

                while (mTimeout >= 0 && !mDSTCallbacks.isReadyToFinish()) {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    mDSTCallbacks.onSleepingTick(mTimeout);

                    mTimeout--;
                }

                mDSTCallbacks.onSleepingEnd();
            }
        }).start();
    }

    public interface iDSTCallbacks {
        void onSleepingStart();
        void onSleepingTick(int counter);
        void onSleepingEnd();
        boolean isReadyToFinish();
        void onError(@NonNull String errorMsg);
    }
}
