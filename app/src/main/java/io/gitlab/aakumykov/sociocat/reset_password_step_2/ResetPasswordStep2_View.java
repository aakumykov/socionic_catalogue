package io.gitlab.aakumykov.sociocat.reset_password_step_2;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.Password;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.gitlab.aakumykov.sociocat.R;
import io.gitlab.aakumykov.sociocat.utils.MyUtils;
import io.gitlab.aakumykov.sociocat.z_base_view.BaseView;


public class ResetPasswordStep2_View extends BaseView implements
        iResetPasswordStep2.View,
        Validator.ValidationListener
{
    @Password(min = 6, scheme = Password.Scheme.ALPHA_NUMERIC_MIXED_CASE_SYMBOLS)
    @BindView(R.id.password1Input) EditText password1Input;

    @ConfirmPassword
    @BindView(R.id.password2Input) EditText password2Input;

    @BindView(R.id.setButton) Button setButton;
    @BindView(R.id.cancelButton) Button cancelButton;

    private iResetPasswordStep2.Presenter presenter;
    private Validator validator;


    // Системные методы
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password_step_2_activity);
        ButterKnife.bind(this);

        setPageTitle(R.string.RESET_PASSWORD_page_title);
        activateUpButton();

        presenter = new ResetPasswordStep2_Presenter();

        validator = new Validator(this);
        validator.setValidationListener(this);
    }

    @Override
    public void onUserGloballyLoggedIn() {

    }

    @Override
    public void onUserGloballyLoggedOut() {

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
        showProgressMessage(R.string.RESET_PASSWORD2_saving_new_password);
        disableForm();

        presenter.setNewPassword(new iResetPasswordStep2.SetNewPasswordCallbacks() {
            @Override
            public void onNewPasswordSetSucces() {
                finishWork();
            }

            @Override
            public void onNewPasswordSetFail(String errorMsg) {
                enableForm();
                showErrorMsg(R.string.RESET_PASSWORD2_error_saving_password, errorMsg);
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
    public String getPassword1() {
        return null;
    }

    @Override
    public String getPassword2() {
        return null;
    }

    @Override
    public void finishWork() {
        showToast(R.string.RESET_PASSWORD2_new_password_saved);
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void disableForm() {
        MyUtils.disable(password1Input);
        MyUtils.disable(password2Input);
        MyUtils.disable(setButton);
//        MyUtils.disable(cancelButton);
    }

    @Override
    public void enableForm() {
        MyUtils.enable(password1Input);
        MyUtils.enable(password2Input);
        MyUtils.enable(setButton);
//        MyUtils.enable(cancelButton);
    }


    // Нажатия
//    @OnClick(R.id.setButton)
//    void sendEmailClicked() {
//        validator.validate();
//    }

//    @OnClick(R.id.cancelButton)
//    void cancelClicked() {
//        setResult(RESULT_CANCELED);
//        finish();
//    }
}
