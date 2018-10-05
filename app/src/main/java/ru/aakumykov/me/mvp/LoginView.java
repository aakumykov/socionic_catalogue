package ru.aakumykov.me.mvp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
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

    @BindViews({R.id.emailInput, R.id.passwordInput, R.id.loginButton, R.id.logoutButton})
    List<View> formElements;

    static final ButterKnife.Action<View> ENABLE = new ButterKnife.Action<View>() {
        @Override
        public void apply(@NonNull View view, int index) {
            view.setEnabled(true);
        }
    };

    static final ButterKnife.Action<View> DISABLE = new ButterKnife.Action<View>() {
        @Override
        public void apply(@NonNull View view, int index) {
            view.setEnabled(false);
        }
    };



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
        ButterKnife.apply(formElements, DISABLE);
    }

    @Override
    public void enableLoginForm() {
        ButterKnife.apply(formElements, ENABLE);
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
