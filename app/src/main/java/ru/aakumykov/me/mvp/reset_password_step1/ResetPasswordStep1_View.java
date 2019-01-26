package ru.aakumykov.me.mvp.reset_password_step1;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.aakumykov.me.mvp.BaseView;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.utils.MyUtils;


public class ResetPasswordStep1_View extends BaseView implements
        iResetPasswordStep1.View,
        Validator.ValidationListener
{
    @Email(messageResId = R.string.RESET_PASSWORD_incorrect_email)
    @BindView(R.id.emailInput) EditText emailInput;

    @BindView(R.id.sendButton) Button sendButton;
    @BindView(R.id.cancelButton) Button cancelButton;

    private iResetPasswordStep1.Presenter presenter;
    private Validator validator;


    // Системные методы
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password_step_1_activity);
        ButterKnife.bind(this);

        setPageTitle(R.string.RESET_PASSWORD_page_title);
        activateUpButton();

        presenter = new ResetPasswordStep1_Presenter();

        validator = new Validator(this);
        validator.setValidationListener(this);
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

    @Override
    public void onValidationSucceeded() {
        showProgressMessage(R.string.RESET_PASSWORD_sending_email);
        disableForm();

        presenter.resetPassword(new iResetPasswordStep1.ResetPasswordCallbacks() {
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


    // Интерфейсные методы
    @Override
    public String getEmail() {
        return emailInput.getText().toString();
    }

    @Override
    public void finishWork() {
        String msg = getResources().getString(R.string.RESET_PASSWORD_email_sended, getEmail());
        showLongToast(msg);
        setResult(RESULT_OK);
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
//        String email = emailInput.getText().toString();
//
//        if (TextUtils.isEmpty(email)) {
//            String text = getResources().getString(R.string.cannot_be_empty);
//            emailInput.setError(text);
//            return;
//        }
//
//        if (!MyUtils.isEmailCorrect(email)) {
//            String text = getResources().getString(R.string.RESET_PASSWORD_incorrect_email);
//            emailInput.setError(text);
//            return;
//        }
//
//        showProgressMessage(R.string.RESET_PASSWORD_sending_email);
//        disableForm();
//
//        presenter.resetPassword(new iResetPasswordStep1.ResetPasswordCallbacks() {
//            @Override
//            public void onEmailSendSucces() {
//                hideProgressBar();
//                hideMsg();
//                finishWork();
//            }
//
//            @Override
//            public void onEmailSendFail(String errorMsg) {
//                showErrorMsg(R.string.RESET_PASSWORD_error_sending_email, errorMsg);
//                enableForm();
//            }
//        });

        validator.validate();
    }

    @OnClick(R.id.cancelButton)
    void cancelClicked() {
        setResult(RESULT_CANCELED);
        finish();
    }
}
