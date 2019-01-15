package ru.aakumykov.me.mvp.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.aakumykov.me.mvp.BaseView;
import ru.aakumykov.me.mvp.register.Register_View;
import ru.aakumykov.me.mvp.utils.MyUtils;
import ru.aakumykov.me.mvp.R;

public class Login_View extends BaseView implements
        iLogin.View {
    @BindView(R.id.emailInput) EditText emailInput;
    @BindView(R.id.passwordInput) EditText passwordInput;
    @BindView(R.id.loginButton) Button loginButton;
    @BindView(R.id.cancelButton) Button cancelButton;
    @BindView(R.id.registerButton) Button registerButton;

    private final static String TAG = "Login_View";
    private iLogin.Presenter presenter;


    // Системные методы
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        ButterKnife.bind(this);

        setPageTitle(getResources().getString(R.string.LOGIN_page_title));
        activateUpButton();

        presenter = new Login_Presenter();
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.linkView(this);
        presenter.processInputIntent(getIntent());
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.unlinkView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                cancelLogin();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    // Обязательные методы
    @Override
    public void onUserLogin() {
        closePage(); // Если пользователь каким-то образом залогинился
    }

    @Override
    public void onUserLogout() {

    }


    // Интерфейсные методы
    @Override
    public void disableForm() {
        MyUtils.disable(emailInput);
        MyUtils.disable(passwordInput);
        MyUtils.disable(loginButton);
        MyUtils.disable(registerButton);
    }

    @Override
    public void enableForm() {
        MyUtils.enable(emailInput);
        MyUtils.enable(passwordInput);
        MyUtils.enable(loginButton);
        MyUtils.enable(registerButton);
    }

    @Override
    public void finishLogin(boolean byCancel) {
        if (byCancel) setResult(RESULT_CANCELED);
        else setResult(RESULT_OK);
        finish();
    }


    // Нажатия
    @OnClick(R.id.loginButton)
    void login() {
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();

        showProgressBar();
        disableForm();
        showInfoMsg(R.string.LOGIN_logging_in);

        presenter.doLogin(email, password);
    }

    @OnClick(R.id.cancelButton)
    void cancelLogin() {
        setResult(RESULT_CANCELED);
        finish();
    }

    @OnClick(R.id.registerButton)
    void goRegisterPage() {
        Intent intent = new Intent(this, Register_View.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }
}
