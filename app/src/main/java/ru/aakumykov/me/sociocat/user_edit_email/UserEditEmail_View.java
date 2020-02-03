package ru.aakumykov.me.sociocat.user_edit_email;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.mobsandgeeks.saripaar.annotation.ConfirmEmail;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.Password;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.aakumykov.me.sociocat.BaseView;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.interfaces.iMyDialogs;
import ru.aakumykov.me.sociocat.user_edit_email.view_model.UserEmailEdit_ViewModel;
import ru.aakumykov.me.sociocat.user_edit_email.view_model.UserEmailEdit_ViewModelFactory;
import ru.aakumykov.me.sociocat.utils.MyDialogs;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class UserEditEmail_View extends BaseView implements iUserEditEmail.iView {

    @Email
    @BindView(R.id.emailInput)
    EditText emailInput;

    @ConfirmEmail
    @BindView(R.id.emailConfirmationInput)
    EditText emailConfirmationInput;

    @Password
    @BindView(R.id.passwordInput)
    EditText passwordInput;

    @BindView(R.id.saveButton) Button saveButton;
    @BindView(R.id.cancelButton) Button cancelButton;

    private iUserEditEmail.iPresenter presenter;

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

            case CHECKING:
                disableForm();
                showProgressMessage(messageId);
                break;

            case EMAIL_ERROR:
                enableForm();
                hideProgressMessage();
                showEmailError(messageId);
                break;

            case PAGE_ERROR:
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
    public void showEmailError(int errorMsgId) {
        emailInput.setError(
                getResources().getString(errorMsgId)
        );
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
        MyUtils.disable(passwordInput);
        MyUtils.disable(saveButton);
    }

    @Override
    public void enableForm() {
        MyUtils.enable(emailInput);
        MyUtils.enable(passwordInput);
        MyUtils.enable(saveButton);
    }

    @Override
    public String getPassword() {
        return passwordInput.getText().toString();
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
