package ru.aakumykov.me.sociocat.login;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.aakumykov.me.sociocat.BaseView;
import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.event_objects.LoginRequestSuccess;
import ru.aakumykov.me.sociocat.register.register_step_1.RegisterStep1_View;
import ru.aakumykov.me.sociocat.reset_password_step1.ResetPasswordStep1_View;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class Login_View extends BaseView implements iLogin.View
{
    @BindView(R.id.emailInput) EditText emailInput;
    @BindView(R.id.passwordInput) EditText passwordInput;
    @BindView(R.id.loginButton) Button loginButton;
    @BindView(R.id.resetPasswordButton) TextView resetPasswordButton;
    @BindView(R.id.registerButton) Button registerButton;

    public static final String TAG = "Login_View";
    private iLogin.Presenter presenter;


    // Системные методы
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        ButterKnife.bind(this);

        setPageTitle(R.string.LOGIN_page_title);
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case Constants.CODE_RESET_PASSWORD:
                afterResetPasswordRequest(resultCode, data);
                break;

            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                presenter.cancelLogin();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        presenter.cancelLogin();
    }


    // Обязательные методы
    @Override
    public void onUserLogin() {
        //closePage(); // Если пользователь каким-то образом залогинился
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
        MyUtils.disable(resetPasswordButton);
        MyUtils.disable(registerButton);
    }

    @Override
    public void enableForm() {
        MyUtils.enable(emailInput);
        MyUtils.enable(passwordInput);
        MyUtils.enable(loginButton);
        MyUtils.enable(resetPasswordButton);
        MyUtils.enable(registerButton);
    }

    @Override
//    public void finishLogin(boolean byCancel, @Nullable Bundle arguments) {
    public void finishLogin(boolean byCancel, @Nullable Bundle bundle) {
        int resultCode = (byCancel) ? RESULT_CANCELED : RESULT_OK;

        Intent resultsIntent = new Intent();
//               resultsIntent.putExtra(Constants.EXTRA_ARGUMENTS, arguments);
        if (null != bundle)
            resultsIntent.putExtra("bundle", bundle);

        setResult(resultCode, resultsIntent);

        finish();
    }

    @Override
    public void notifyToConfirmEmail(String userId) {
<<<<<<< HEAD
=======

    }

    @Override
    public void proceedLoginRequest(Intent intent) {
        Intent originalIntent = (Intent) intent.getParcelableExtra(Intent.EXTRA_INTENT);
        String requestedAction = intent.getStringExtra(Constants.EXTRA_REQUESTED_ACTION);

        if ("add_comment".equals(requestedAction)) {
            EventBus.getDefault().post(new LoginRequestSuccess(requestedAction));
            return;
        }
>>>>>>> 8a716d7d65ec3c465f2f779f35f64ead59def73a

        startActivity(originalIntent);
    }


    // Нажатия
    @OnClick(R.id.loginButton)
    void login() {
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();

        disableForm();
        showProgressMessage(R.string.LOGIN_logging_in);

        presenter.doLogin(email, password);
    }

    @OnClick(R.id.resetPasswordButton)
    void resetPassword() {
        Intent intent = new Intent(this, ResetPasswordStep1_View.class);
        startActivityForResult(intent, Constants.CODE_RESET_PASSWORD);
    }

    @OnClick(R.id.registerButton)
    void goRegisterPage() {
        Intent intent = new Intent(this, RegisterStep1_View.class);
        startActivity(intent);
    }


    // Внтуренния методы
    private void afterResetPasswordRequest(int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                showToast(R.string.LOGIN_password_recovery_email_sent);
                break;
            case RESULT_CANCELED:
                hideMsg();
                break;
            default:
                Log.e(TAG,"Unknown result code: "+resultCode);
                break;
        }
    }
}
