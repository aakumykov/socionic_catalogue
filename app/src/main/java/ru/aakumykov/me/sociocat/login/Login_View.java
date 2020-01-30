package ru.aakumykov.me.sociocat.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.aakumykov.me.sociocat.BaseView;
import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.login.view_model.Login_ViewModel;
import ru.aakumykov.me.sociocat.login.view_model.Login_ViewModelFactory;
import ru.aakumykov.me.sociocat.other.VKInteractor;
import ru.aakumykov.me.sociocat.register_step_1.RegisterStep1_View;
import ru.aakumykov.me.sociocat.reset_password_step1.ResetPasswordStep1_View;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class Login_View extends BaseView implements iLogin.View
{
    @BindView(R.id.emailView) EditText emailInput;
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

        Login_ViewModel viewModel = new ViewModelProvider(this, new Login_ViewModelFactory())
                .get(Login_ViewModel.class);

        if (viewModel.hasPresenter()) {
            this.presenter = viewModel.getPresenter();
        } else {
            this.presenter = new Login_Presenter();
            viewModel.storePresenter(this.presenter);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.linkView(this);

        if (presenter.isVirgin())
            presenter.processInputIntent(getIntent());
        else
            presenter.onConfigChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.unlinkView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

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

        if (VKInteractor.isVKActivityResult(requestCode, resultCode, data, loginVKCallbacks)) {
            // Обработка происходит в loginVKCallbacks
        }
        else {
            switch (requestCode) {
                default:
                    super.onActivityResult(requestCode, resultCode, data);
                    break;
            }
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
        // Это должен обрабатывать presenter
    }

    @Override
    public void onUserLogout() {

    }


    @Override
    public void setViewState(iLogin.ViewState state, int messageId) {
        setViewState(state, messageId, null);
    }

    @Override
    public void setViewState(iLogin.ViewState state, int messageId, @Nullable String messageDetails) {

        presenter.storeViewState(state, messageId, messageDetails);

        switch (state) {
            case INITIAL:
                hideProgressMessage();
                enableForm();
                break;

            case PROGRESS:
                break;

            case SUCCESS:
                break;

            case ERROR:
                break;

            default:
                Log.e(TAG, "Unknown viewState: "+state);
        }
    }

    // Интерфейсные методы
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
    public void finishLogin(boolean isCancelled, Intent transitIntent) {
        int resultCode = (isCancelled) ? RESULT_CANCELED : RESULT_OK;

        Intent resultsIntent = null;

        if (null != transitIntent) {
            resultsIntent = transitIntent;
        }

        if (null != resultsIntent)
            setResult(resultCode, resultsIntent);
        else
            setResult(resultCode);

        finish();
    }

    @Override
    public void notifyToConfirmEmail(String userId) {
        showErrorMsg(R.string.LOGIN_email_confirmation_required, "(●'◡'●)");
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
                hideMessage();
                break;
            default:
                Log.e(TAG,"Unknown result code: "+resultCode);
                break;
        }
    }

}
