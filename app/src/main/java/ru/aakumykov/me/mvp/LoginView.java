package ru.aakumykov.me.mvp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class LoginView extends AppCompatActivity implements
        android.view.View.OnClickListener, iLogin.View {

    private final static String TAG = "myLog";
    iLogin.Presenter presenter;

    private ProgressBar progressBar;
    private TextView infoView;
    private TextView errorView;
    private EditText emailInput;
    private EditText passwordInput;
    private Button loginButton;
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "=CREATE");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progressBar);
        infoView = findViewById(R.id.infoView);
        errorView = findViewById(R.id.errorView);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        logoutButton = findViewById(R.id.logoutButton);

        loginButton.setOnClickListener(this);
        logoutButton.setOnClickListener(this);

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

            case R.id.logoutButton:
                presenter.logoutButtonClicked();
                break;

            default:
                break;
        }
    }


    @Override
    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }
    @Override
    public void hideProgressBar() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showInfo(String message) {
        hideError();
        infoView.setText(message);
        infoView.setVisibility(View.VISIBLE);
    }
    @Override
    public void showInfo(int messageId) {
        showInfo(getString(messageId));
    }

    @Override
    public void hideInfo() {
        infoView.setVisibility(View.GONE);
    }

    @Override
    public void showError(String message) {
        hideInfo();
        errorView.setText(message);
        errorView.setVisibility(View.VISIBLE);
    }
    @Override
    public void showError(int messageId) {
        showError(getString(messageId));
    }

    @Override
    public void hideError() {
        errorView.setVisibility(View.GONE);
    }

    @Override
    public void disableLoginForm() {
        emailInput.setEnabled(false);
        passwordInput.setEnabled(false);
        loginButton.setEnabled(false);
        logoutButton.setEnabled(false);
    }

    @Override
    public void enableLoginForm() {
        emailInput.setEnabled(true);
        passwordInput.setEnabled(true);
        loginButton.setEnabled(true);
        logoutButton.setEnabled(true);
    }

    @Override
    public String getEmail() {
        return emailInput.getText().toString();
    }
    @Override
    public String getPassword() {
        return passwordInput.getText().toString();
    }

}
