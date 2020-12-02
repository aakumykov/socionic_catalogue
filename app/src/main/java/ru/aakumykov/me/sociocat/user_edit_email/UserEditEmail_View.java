package ru.aakumykov.me.sociocat.user_edit_email;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.ConfirmEmail;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.Password;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.user_edit_email.view_model.UserEmailEdit_ViewModel;
import ru.aakumykov.me.sociocat.user_edit_email.view_model.UserEmailEdit_ViewModelFactory;
import ru.aakumykov.me.sociocat.utils.MyUtils;
import ru.aakumykov.me.sociocat.utils.my_dialogs.MyDialogs;
import ru.aakumykov.me.sociocat.utils.my_dialogs.iMyDialogs;
import ru.aakumykov.me.sociocat.z_base_view.BaseView;

public class UserEditEmail_View extends BaseView implements
        iUserEditEmail.iView,
        Validator.ValidationListener
{
    @BindView(R.id.emailInput) @Email(messageResId = R.string.error_incorrect_email)
    EditText emailInput;

    @BindView(R.id.emailConfirmationInput) @ConfirmEmail(messageResId = R.string.error_email_not_match)
    EditText emailConfirmationInput;

    @BindView(R.id.passwordInput) @Password(messageResId = R.string.error_password_is_empty)
    EditText passwordInput;

    @BindView(R.id.saveButton) Button saveButton;
    @BindView(R.id.cancelButton) Button cancelButton;

    private iUserEditEmail.iPresenter presenter;
    private Validator validator;


    // Activity
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_edit_email_activity);
        ButterKnife.bind(this);

        setPageTitle(R.string.USER_EDIT_EMAIL_page_title);
        activateUpButton();

        UserEmailEdit_ViewModel viewModel = new ViewModelProvider(this, new UserEmailEdit_ViewModelFactory())
                .get(UserEmailEdit_ViewModel.class);

        if (viewModel.hasPresenter()) {
            this.presenter = viewModel.getPresenter();
        } else {
            this.presenter = new UserEditEmail_Presenter();
            viewModel.storePresenter(this.presenter);
        }

        validator = new Validator(this);
        validator.setValidationListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        presenter.linkView(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.linkView(this);

        if (presenter.isVirgin())
            presenter.onFirstOpen();
        else
            presenter.onConfigChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.unlinkView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                return presenter.onHomePressed();

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        presenter.onBackPressed();
    }


    // BaseView
    @Override
    public void onUserLogin() {

    }

    @Override
    public void onUserLogout() {
        finish();
    }

    @Override
    public void setViewState(iUserEditEmail.ViewState state, int messageId) {
        setViewState(state, messageId, null);
    }

    @Override
    public void setViewState(iUserEditEmail.ViewState state, int messageId, @Nullable String messageDetails) {

        presenter.storeViewState(state, messageId, messageDetails);

        switch (state) {
            case SUCCESS:
                showSuccessDialog(messageDetails);
                break;

            case PROGRESS:
                disableForm();
                showProgressMessage(messageId);
                break;

            case EMAIL_ERROR:
                enableForm();
                hideProgressMessage();
                showEmailError(messageId);
                break;

            case ERROR:
                enableForm();
                showErrorMsg(messageId, messageDetails);
                break;

            default:
                break;
        }
    }


    // iUserEditEmail.iView
    @Override
    public void displayCurrentEmail(String email) {
        emailInput.setText(email);
    }

    @Override
    public String getEmail() {
        return emailInput.getText().toString();
    }

    @Override
    public void validateForm() {
        validator.validate();
    }

    @Override
    public void showEmailError(int errorMsgId) {
        String message = getResources().getString(errorMsgId);
        emailInput.setError(message);
        emailConfirmationInput.setError(message);
    }

    @Override
    public void showPasswordError(int errorMsgId) {
        passwordInput.setError(
                getResources().getString(errorMsgId)
        );
    }

    @Override
    public void disableForm() {
        MyUtils.disable(emailInput);
        MyUtils.disable(emailConfirmationInput);
        MyUtils.disable(passwordInput);
        MyUtils.disable(saveButton);
    }

    @Override
    public void enableForm() {
        MyUtils.enable(emailInput);
        MyUtils.enable(emailConfirmationInput);
        MyUtils.enable(passwordInput);
        MyUtils.enable(saveButton);
    }

    @Override
    public String getPassword() {
        return passwordInput.getText().toString();
    }


    // Saripaar Validator
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
    @OnClick(R.id.saveButton)
    void onSaveButtonClicked() {
        presenter.onSaveButtonClicked();
    }

    @OnClick(R.id.cancelButton)
    void onCancelButtonClicked() {
        presenter.onCancelButtonClicked();
    }


    // Внутренние
    private void showSuccessDialog(String newEmailAddress) {
        hideProgressMessage();

        String message = getString(R.string.USER_EDIT_EMAIL_verification_sent_message, newEmailAddress);

        MyDialogs.infoDialog(
                this,
                R.string.USER_EDIT_EMAIL_verification_sent_title,
                message,
                new iMyDialogs.StandardCallbacks() {
                    @Override
                    public void onCancelInDialog() {

                    }

                    @Override
                    public void onNoInDialog() {

                    }

                    @Override
                    public boolean onCheckInDialog() {
                        return false;
                    }

                    @Override
                    public void onYesInDialog() {
                        closePage(RESULT_OK, Intent.ACTION_EDIT);
                    }
                }
        );
    }


}
