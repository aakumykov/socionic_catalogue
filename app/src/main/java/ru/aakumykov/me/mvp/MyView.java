package ru.aakumykov.me.mvp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MyView extends AppCompatActivity implements View.OnClickListener{

    private final static String TAG = "myLog";
    MyPresenter myPresenter;

    private TextView textView;
    private EditText textInput;
    private Button setButton;
    private Button clearButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "=CREATE");

        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);
        textInput = findViewById(R.id.textInput);
        setButton = findViewById(R.id.setButton);
        clearButton = findViewById(R.id.clearButton);

        setButton.setOnClickListener(this);
        clearButton.setOnClickListener(this);

        myPresenter = (MyPresenter) getLastCustomNonConfigurationInstance();
        if (null == myPresenter)
            myPresenter = new MyPresenter();
        myPresenter.linkView(this);
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return myPresenter;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "=DESTROY");
        myPresenter.unlinkView();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.setButton:
                myPresenter.setButtonClicked();
                break;
            case R.id.clearButton:
                myPresenter.clearButtonClicked();
                break;
            default:
                break;
        }
    }

    public void displayText(String text) {
        this.textView.setText(text);
    }

    public void clearTextInput() {
        textInput.setText("");
    }

    public void reset() {
        textView.setText(R.string.defaultText);
        textInput.setText("");
    }

    public String getNewText() {
        return this.textInput.getText().toString();
    }

}
