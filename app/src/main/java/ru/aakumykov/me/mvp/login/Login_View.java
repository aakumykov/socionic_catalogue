package ru.aakumykov.me.mvp.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.aakumykov.me.mvp.BaseView;
import ru.aakumykov.me.mvp.utils.MyUtils;
import ru.aakumykov.me.mvp.R;

public class Login_View extends BaseView implements
        iLogin.View {
    @BindView(R.id.emailInput) EditText emailInput;
    @BindView(R.id.passwordInput) EditText passwordInput;
    @BindView(R.id.loginButton) Button loginButton;
    @BindView(R.id.cancelButton) Button cancelButton;

    private final static String TAG = "Login_View";
    private iLogin.Presenter presenter;


    // Системные методы
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        ButterKnife.bind(this);

        setPageTitle(getResources().getString(R.string.LOGIN_page_title));
        enableUpButton();

        presenter = new Login_Presenter();
    }

    @Override
    public void onServiceBounded() {
        presenter.linkView(this);
        presenter.linkCardsService(getCardsService());
        presenter.linkAuth(getAuthService());
    }

    @Override
    public void onServiceUnbounded() {
        presenter.unlinkView();
        presenter.unlinkCardsService();
        presenter.unlinkAuthService();
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


    // Интерфейсные методы
    @Override
    public void disableForm() {
        MyUtils.disable(emailInput);
        MyUtils.disable(passwordInput);
        MyUtils.disable(loginButton);
    }

    @Override
    public void enableForm() {
        MyUtils.enable(emailInput);
        MyUtils.enable(passwordInput);
        MyUtils.enable(loginButton);
    }

    @Override
    public void finishLogin(boolean byCancel) {
        if (byCancel) setResult(RESULT_CANCELED);
        else setResult(RESULT_OK);
        finish();
    }


    // Обработчики нажатий
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
}
