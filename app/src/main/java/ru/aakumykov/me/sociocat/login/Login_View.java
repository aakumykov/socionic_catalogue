package ru.aakumykov.me.sociocat.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.SignInButton;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.Password;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.aakumykov.me.sociocat.base_view.BaseView;
import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.login.view_model.Login_ViewModel;
import ru.aakumykov.me.sociocat.login.view_model.Login_ViewModelFactory;
import ru.aakumykov.me.sociocat.register_step_1.RegisterStep1_View;
import ru.aakumykov.me.sociocat.register_step_2.RegisterStep2_View;
import ru.aakumykov.me.sociocat.reset_password_step1.ResetPasswordStep1_View;
import ru.aakumykov.me.sociocat.utils.MyUtils;
import ru.aakumykov.me.sociocat.utils.auth.GoogleAuthHelper;

public class Login_View extends BaseView implements
        iLogin.View,
        Validator.ValidationListener
{
    @BindView(R.id.tryNewPasswordMessageView) TextView tryNewPasswordMessageView;

    @Email(messageResId = R.string.error_incorrect_email)
    @BindView(R.id.emailView) EditText emailInput;

    @Password(messageResId = R.string.cannot_be_empty)
    @BindView(R.id.passwordInput) EditText passwordInput;

    @BindView(R.id.loginButton) Button loginButton;
    @BindView(R.id.resetPasswordButton) TextView resetPasswordButton;
    @BindView(R.id.registerButton) Button registerButton;
    @BindView(R.id.cancelButton) Button cancelButton;
    @BindView(R.id.googleLoginButton) SignInButton googleLoginButton;

    public static final String TAG = "Login_View";
    private iLogin.Presenter presenter;
    private Validator validator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        ButterKnife.bind(this);

        setPageTitle(R.string.LOGIN_page_title);
        activateUpButton();

        googleLoginButton.setSize(SignInButton.SIZE_WIDE);

        Login_ViewModel viewModel = new ViewModelProvider(this, new Login_ViewModelFactory())
                .get(Login_ViewModel.class);

        if (viewModel.hasPresenter()) {
            this.presenter = viewModel.getPresenter();
        } else {
            this.presenter = new Login_Presenter();
            viewModel.storePresenter(this.presenter);
        }

        validator = new Validator(this);
        validator.setValidationListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.linkView(this);

        if (presenter.isVirgin())
            presenter.processInputIntent(getIntent());
        else
            presenter.onConfigChanged();

        updateGoogleLoginButtons();
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.unlinkView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
                case Constants.CODE_USER_EDIT:
                    goToMainPage();
                    break;
                case Constants.CODE_GOOGLE_LOGIN:
                    processGoogleLoginResult(resultCode, data);
                    break;
                default:
                    super.onActivityResult(requestCode, resultCode, data);
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

    @Override
    public void onUserLogin() {
        updateGoogleLoginButtons();
    }

    @Override
    public void onUserLogout() {
        updateGoogleLoginButtons();
    }

    @Override
    public void setState(iLogin.ViewState state, int messageId) {
        setState(state, messageId, null);
    }

    @Override
    public void setState(iLogin.ViewState state, int messageId, @Nullable String messageDetails) {

        presenter.storeViewState(state, messageId, messageDetails);

        switch (state) {
            case INITIAL:
                hideProgressMessage();
                enableForm();
                break;

            case INFO:
                enableForm();
                MyUtils.show(tryNewPasswordMessageView);
                break;

            case PROGRESS:
                disableForm();
                showProgressMessage(messageId);
                break;

            case SUCCESS:
                showToast(messageId);
                break;

            case ERROR:
                enableForm();
                showErrorMsg(messageId, messageDetails);
                break;

            default:
                Log.e(TAG, "Unknown viewState: "+state);
        }
    }

    @Override
    public void startLoginWithGoogle() {
        Intent signInIntent = GoogleAuthHelper.getSignInIntent(this);
        startActivityForResult(signInIntent, Constants.CODE_GOOGLE_LOGIN);
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
    public String getEmail() {
        return emailInput.getText().toString();
    }

    @Override
    public String getPassword() {
        return passwordInput.getText().toString();
    }

    @Override
    public void go2finishRegistration(@NonNull String userId) {
        Intent intent = new Intent(this, RegisterStep2_View.class);
        intent.putExtra(Constants.USER_ID, userId);
        startActivity(intent);
    }


    // Validator.ValidationListener
    @Override
    public void onValidationSucceeded() {
        presenter.onFormIsValid();
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                showToast(message);
            }
        }
    }


    // Нажатия
    @OnClick(R.id.loginButton)
    void onLoginButtonClicked() {
        validator.validate();
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

    @OnClick(R.id.googleLoginButton)
    void onGoogleLoginButtonClicked() {
        presenter.onLoginWithGoogleClicked();
    }


    // Внтуренния методы
    private void disableForm() {
        MyUtils.disable(emailInput);
        MyUtils.disable(passwordInput);
        MyUtils.disable(loginButton);
        MyUtils.disable(resetPasswordButton);
        MyUtils.disable(registerButton);
        MyUtils.disable(googleLoginButton);
    }

    private void enableForm() {
        MyUtils.enable(emailInput);
        MyUtils.enable(passwordInput);
        MyUtils.enable(loginButton);
        MyUtils.enable(resetPasswordButton);
        MyUtils.enable(registerButton);
        MyUtils.enable(googleLoginButton);
    }

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

    private void processGoogleLoginResult(int resultCode, @Nullable Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                presenter.onGoogleLoginResult(data);
                break;
            case RESULT_CANCELED:
                break;
            default:
                Log.e(TAG, "Unknown result code: "+resultCode);
        }
    }

    private void updateGoogleLoginButtons() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (null == account) {
            MyUtils.show(googleLoginButton);
        }
        else {
            MyUtils.hide(googleLoginButton);
        }
    }
}
