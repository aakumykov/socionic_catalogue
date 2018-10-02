package ru.aakumykov.me.mvp;

import android.text.TextUtils;
import android.util.Log;

class MyPresenter {

    private final static String TAG = "myLog";
    private MyView view;
    private MyModel model;

    MyPresenter() {
        Log.d(TAG, "=MyPresenter()=");
        this.model = new MyModel();
    }

    void linkView(MyView view) {
        Log.d(TAG, "=linkView()=");
        this.view = view;
    }

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
