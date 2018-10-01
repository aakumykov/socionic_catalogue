package ru.aakumykov.me.mvp;

import android.text.TextUtils;
import android.util.Log;

public class MyPresenter {

    private final static String TAG = "myLog";
    private MyView view;
    private MyModel model;
    private String lastText;

    MyPresenter() {
        Log.d(TAG, "=MyPresenter()=");
        this.model = new MyModel();
    }

    public void linkView(MyView view) {
        Log.d(TAG, "=linkView()=");
        this.view = view;
        Log.d(TAG, this.view.toString());
        if (!TextUtils.isEmpty(lastText))
            view.displayText(lastText);
    }

    public void unlinkView() {
        Log.d(TAG, "=unlinkView()=");
        this.view = null;
    }

    public void setButtonClicked() {
        String newText = view.getNewText();
        if (!TextUtils.isEmpty(newText)) {
            lastText = newText;
            String processedText = model.processText(newText);
            view.displayText(processedText);
            view.clearTextInput();
        }
    }

    public void clearButtonClicked() {
        view.reset();
    }
}
