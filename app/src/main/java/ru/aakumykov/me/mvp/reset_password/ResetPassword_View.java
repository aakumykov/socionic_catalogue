package ru.aakumykov.me.mvp.reset_password;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.aakumykov.me.mvp.BaseView;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.utils.MyUtils;


public class ResetPassword_View extends BaseView implements
        iResetPassword.View
{
    @BindView(R.id.emailInput) EditText emailInput;
    @BindView(R.id.sendButton) Button sendButton;
    @BindView(R.id.cancelButton) Button cancelButton;
    private iResetPassword.Presenter presenter;


    // Системные методы
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password_activity);
        ButterKnife.bind(this);

        setPageTitle(R.string.RESET_PASSWORD_page_title);
        activateUpButton();

        presenter = new ResetPassword_Presenter();
    }

    @Override
    public void onUserLogin() {

    }

    @Override
    public void onUserLogout() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.linkView(this);
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


    // Интерфейсные методы
    @Override
    public String getEmail() {
        return emailInput.getText().toString();
    }

    @Override
    public void finishWork() {
        showToast(R.string.RESET_PASSWORD_email_sended);
        finish();
    }

    @Override
    public void disableForm() {
        MyUtils.disable(emailInput);
        MyUtils.disable(sendButton);
        MyUtils.disable(cancelButton);
    }

    @Override
    public void enableForm() {
        MyUtils.enable(emailInput);
        MyUtils.enable(sendButton);
        MyUtils.enable(cancelButton);
    }


    // Нажатия
    @OnClick(R.id.sendButton)
    void sendEmailClicked() {
        String email = emailInput.getText().toString();

        if (TextUtils.isEmpty(email)) {
            String text = getResources().getString(R.string.cannot_be_empty);
            emailInput.setError(text);
            return;
        }

        if (!MyUtils.isEmailCorrect(email)) {
            String text = getResources().getString(R.string.RESET_PASSWORD_incorrect_email);
            emailInput.setError(text);
            return;
        }

        showProgressMessage(R.string.RESET_PASSWORD_sending_email);
        disableForm();

        presenter.resetPassword(new iResetPassword.ResetPasswordCallbacks() {
            @Override
            public void onEmailSendSucces() {
                hideProgressBar();
                hideMsg();
                finishWork();
            }

            @Override
            public void onEmailSendFail(String errorMsg) {
                showErrorMsg(R.string.RESET_PASSWORD_error_sending_email, errorMsg);
                enableForm();
            }
        });
    }

    @OnClick(R.id.cancelButton)
    void cancelClicked() {
        finish();
    }
}
