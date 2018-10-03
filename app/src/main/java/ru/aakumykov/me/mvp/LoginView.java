package ru.aakumykov.me.mvp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginView extends AppCompatActivity implements
        android.view.View.OnClickListener, iLogin.View {

    private final static String TAG = "myLog";
    iLogin.Presenter presenter;

    private TextView infoView;
    private TextView errorView;
    private EditText nameInput;
    private EditText passwordInput;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "=CREATE");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        infoView = findViewById(R.id.infoView);
        errorView = findViewById(R.id.errorView);
        nameInput = findViewById(R.id.nameInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(this);

        presenter = new LoginPresenter();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "=START");
        presenter.linkView(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "=STOP");
        presenter.unlinkView();
    }

    @Override
    public void onClick(android.view.View view) {
        switch (view.getId()) {

            case R.id.loginButton:
                presenter.loginButtonClicked();
                break;

            default:
                break;
        }
    }


    @Override
    public void showInfo(String message) {
        infoView.setText(message);
        infoView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideInfo() {
        infoView.setVisibility(View.GONE);
    }

    @Override
    public void showError(String message) {
        errorView.setText(message);
        errorView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideError() {
        errorView.setVisibility(View.GONE);
    }

    @Override
    public void disableLoginButton() {
        loginButton.setEnabled(false);
    }

    @Override
    public void enableLoginButton() {
        loginButton.setEnabled(true);
    }

    @Override
    public String getName() {
        return nameInput.getText().toString();
    }
}
