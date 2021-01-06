package ru.aakumykov.me.sociocat.z_rules_test;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class SleepingThread extends Thread {

    private int mTimeToSleep;
    private boolean mIsFinishCondition = false;
    private final Callable<Boolean> mFinishConditionCheckCallable;
    private final Runnable mOnFinishActionRunnable;


    public SleepingThread(int maxSecondsToWait, Callable<Boolean> finishCondition, Runnable onFinishAction) {
        mTimeToSleep = maxSecondsToWait;
        mFinishConditionCheckCallable = finishCondition;
        mOnFinishActionRunnable = onFinishAction;
    }

    @Override
    public synchronized void start() {
        while (mTimeToSleep > 0 && !mIsFinishCondition) {
            sleepAPieceOfTime();
        }
        mOnFinishActionRunnable.run();
    }

    private void sleepAPieceOfTime() {
        try {
            TimeUnit.SECONDS.sleep(1);
            decreaseTimeCounter();
            checkFinishCondition();
        }
        catch (InterruptedException e) {
            setFinishFlag();
        }
    }

    private void setFinishFlag() {
        mIsFinishCondition = true;
    }

    private void decreaseTimeCounter() {
        mTimeToSleep--;
    }

    private void checkFinishCondition() {
        try {
            mIsFinishCondition = mFinishConditionCheckCallable.call();
        } catch (Exception e) {
            setFinishFlag();
        }
    }

    private void sleepWhileHasTime() {

        boolean isFinishCondition = false;

        try {
            isFinishCondition = mFinishConditionCheckCallable.call();
        } catch (Exception e) {
            isFinishCondition = true;
        }

        if (0 == mTimeToSleep || isFinishCondition) {
            mOnFinishActionRunnable.run();
            return;
        }
        else {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                mOnFinishActionRunnable.run();
                return;
            }

            mTimeToSleep--;
            sleepWhileHasTime();
        }
    }
}
