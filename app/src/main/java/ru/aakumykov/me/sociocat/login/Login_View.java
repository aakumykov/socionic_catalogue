package ru.aakumykov.me.sociocat.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.aakumykov.me.sociocat.BaseView;
import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.other.VKInteractor;
import ru.aakumykov.me.sociocat.register.register_step_1.RegisterStep1_View;
import ru.aakumykov.me.sociocat.reset_password_step1.ResetPasswordStep1_View;
import ru.aakumykov.me.sociocat.singletons.AuthSingleton;
import ru.aakumykov.me.sociocat.singletons.iAuthSingleton;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class Login_View extends BaseView implements iLogin.View
{
    @BindView(R.id.emailInput) EditText emailInput;
    @BindView(R.id.passwordInput) EditText passwordInput;
    @BindView(R.id.loginButton) Button loginButton;
    @BindView(R.id.resetPasswordButton) TextView resetPasswordButton;
    @BindView(R.id.registerButton) Button registerButton;
    @BindView(R.id.cancelButton) Button cancelButton;
    @BindView(R.id.vkLoginButton) ImageView vkLoginButton;

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

        VKInteractor.LoginVK_Callbacks loginVKCallbacks = new VKInteractor.LoginVK_Callbacks() {
            @Override
            public void onVKLoginSuccess(VKInteractor.VKAuthResult vkAuthResult) {
                String vk_access_token = vkAuthResult.getAccessToken();
                int vk_user_id = vkAuthResult.getUserId();
                presenter.processVKLogin(vk_user_id, vk_access_token);
            }

            @Override
            public void onVKLoginError(int errorCode, @Nullable String errorMsg) {
                showErrorMsg(R.string.LOGIN_error_login_via_vkontakte, errorMsg);
            }
        };

        if (!VKInteractor.isVKActivityResult(requestCode, resultCode, data, loginVKCallbacks)) {

            switch (requestCode) {
                case Constants.CODE_RESET_PASSWORD:
                    afterResetPasswordRequest(resultCode, data);
                    break;
                default:
                    break;
            }
        }

/*
        switch (requestCode) {
            case Constants.CODE_RESET_PASSWORD:
                afterResetPasswordRequest(resultCode, data);
                break;
            default:
                break;
        }
*/
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
    public Activity getActivity() {
        return this;
    }

    @Override
    public void disableForm() {
        MyUtils.disable(emailInput);
        MyUtils.disable(passwordInput);
        MyUtils.disable(loginButton);
        MyUtils.disable(resetPasswordButton);
        MyUtils.disable(registerButton);
//        MyUtils.disable(cancelButton);
        MyUtils.disable(vkLoginButton);
    }

    @Override
    public void enableForm() {
        MyUtils.enable(emailInput);
        MyUtils.enable(passwordInput);
        MyUtils.enable(loginButton);
        MyUtils.enable(resetPasswordButton);
        MyUtils.enable(registerButton);
//        MyUtils.enable(cancelButton);
        MyUtils.enable(vkLoginButton);
    }

    @Override
    public void finishLogin(boolean isCancelled, Intent transitIntent, @Nullable Bundle transitArguments) {
        int resultCode = (isCancelled) ? RESULT_CANCELED : RESULT_OK;

        Intent resultsIntent = null;

        if (null != transitIntent) {
            resultsIntent = transitIntent;
        }

        if (null != transitArguments) {
            resultsIntent = new Intent();
            resultsIntent.putExtra(Constants.TRANSIT_ARGUMENTS, transitArguments);
        }

        if (null != resultsIntent)
            setResult(resultCode, resultsIntent);
        else
            setResult(resultCode);

        finish();
    }

    @Override
    public void notifyToConfirmEmail(String userId) {

    }


    // Нажатия
    @OnClick(R.id.loginButton)
    void onLoginButtonClicked() {
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();

        disableForm();
        showProgressMessage(R.string.LOGIN_logging_in);

        presenter.doLogin(email, password);
    }

    @OnClick(R.id.resetPasswordButton)
    void onResetPasswordButtonClicked() {
        Intent intent = new Intent(this, ResetPasswordStep1_View.class);
        startActivityForResult(intent, Constants.CODE_RESET_PASSWORD);
    }

    @OnClick(R.id.registerButton)
    void onGoRegisterPageClicked() {
        Intent intent = new Intent(this, RegisterStep1_View.class);
        startActivity(intent);
    }

    @OnClick(R.id.cancelButton)
    void onCancelLoginButtonClicked() {
        presenter.cancelLogin();
    }

    @OnClick(R.id.vkLoginButton)
    void onVKLoginButtonClicked() {
        presenter.onVKLoginButtonClicked();
    }

    // Убрать...
    @OnClick(R.id.vkLogoutButton)
    void onVKLogoutButtonClicked() {
        VKInteractor.logout(new VKInteractor.LogoutVK_Callbacks() {
            @Override
            public void onVKLogoutSuccess() {
                showToast("Вы вышли из ВК");
            }

            @Override
            public void onVKLogoutError() {
                showDebugMsg("ОШИБКА выхода из ВК");
            }
        });
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
