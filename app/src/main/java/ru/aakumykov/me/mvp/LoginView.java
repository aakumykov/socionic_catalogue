package ru.aakumykov.me.mvp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginView extends AppCompatActivity implements
        android.view.View.OnClickListener, iLogin.View {

    private final static String TAG = "myLog";
    private iLogin.Presenter presenter;

    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.infoView) TextView infoView;
    @BindView(R.id.errorView) TextView errorView;
    @BindView(R.id.emailInput) EditText emailInput;
    @BindView(R.id.passwordInput) EditText passwordInput;
    @BindView(R.id.loginButton) Button loginButton;
    @BindView(R.id.logoutButton) Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "=CREATE");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

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
    @OnClick({R.id.loginButton, R.id.logoutButton})
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
