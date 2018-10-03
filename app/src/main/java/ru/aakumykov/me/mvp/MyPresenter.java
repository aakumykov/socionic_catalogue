package ru.aakumykov.me.mvp;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.text.TextUtils;
import android.util.Log;

import static android.arch.lifecycle.Lifecycle.State.CREATED;
import static android.arch.lifecycle.Lifecycle.State.STARTED;

class MyPresenter implements LifecycleObserver {

    private final static String TAG = "myLog";
    private MyView view;
    private MyModel model;

    MyPresenter(Lifecycle lifecycle) {
        Log.d(TAG, "=MyPresenter()=");

        this.model = new MyModel();

        if (lifecycle.getCurrentState().isAtLeast(CREATED)) {
            lifecycle.addObserver(this);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    void linkView(MyView view) {
        Log.d(TAG, "=linkView()=");
        this.view = view;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    void unlinkView() {
        Log.d(TAG, "=unlinkView()=");
        this.view = null;
    }

    void setButtonClicked() {
        String newText = view.getNewText();
        if (!TextUtils.isEmpty(newText)) {
            String processedText = model.processText(newText);
            view.displayText(processedText);
            view.clearTextInput();
        }
    }

    void clearButtonClicked() {
        view.reset();
    }
}
