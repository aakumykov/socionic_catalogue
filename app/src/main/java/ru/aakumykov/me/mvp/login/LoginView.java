package ru.aakumykov.me.mvp.login;

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
import ru.aakumykov.me.mvp.R;

public class LoginView extends AppCompatActivity implements
        android.view.View.OnClickListener, iLogin.View {

    private final static String TAG = "myLog";
    private iLogin.Presenter presenter;

    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.messageView) TextView messageView;
    @BindView(R.id.aboutInput) EditText emailInput;
    @BindView(R.id.passwordInput) EditText passwordInput;
    @BindView(R.id.loginButton) Button loginButton;
    @BindView(R.id.logoutButton) Button logoutButton;

    @BindViews({R.id.aboutInput, R.id.passwordInput, R.id.loginButton, R.id.logoutButton})
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
        Log.d(TAG, "=CREATE=");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        ButterKnife.bind(this);

        loginButton.setOnClickListener(this);
        logoutButton.setOnClickListener(this);

        presenter = new LoginPresenter();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "=START=");
        presenter.linkView(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "=STOP=");
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
    public void showInfo(int messageId) {
        showMessage(messageId, R.color.info);
    }
    @Override
    public void hideInfo() {
        hideMessage();
    }


    @Override
    public void showWarning(int messageId) {
        showMessage(messageId, R.color.warning);
    }
    @Override
    public void hideWarning() {
        hideMessage();
    }


    @Override
    public void showError(int messageId) {
        showMessage(messageId, R.color.error);
    }
    @Override
    public void hideError() {
        hideMessage();
    }


    private void showMessage(int messageId, int colorId) {
        String textMsg = getResources().getString(messageId);
        showMessage(textMsg, colorId);
    }
    private void showMessage(String message, int colorId) {
        messageView.setTextColor(getResources().getColor(colorId));
        messageView.setText(message);
        messageView.setVisibility(View.VISIBLE);
    }
    private void hideMessage() {
        messageView.setVisibility(View.GONE);
    }


    @Override
    public void enableLoginForm() {
        ButterKnife.apply(formElements, ENABLE);
    }
    @Override
    public void disableLoginForm() {
        ButterKnife.apply(formElements, DISABLE);
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
