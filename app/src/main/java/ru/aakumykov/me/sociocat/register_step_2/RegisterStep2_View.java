package ru.aakumykov.me.sociocat.register_step_2;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.aakumykov.me.sociocat.BaseView;
import ru.aakumykov.me.sociocat.Config;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.interfaces.iDialogCallbacks;
import ru.aakumykov.me.sociocat.register_step_2.view_model.RegisterStep2_ViewModel;
import ru.aakumykov.me.sociocat.register_step_2.view_model.RegisterStep2_ViewModelFactory;
import ru.aakumykov.me.sociocat.utils.MyUtils;
import ru.aakumykov.me.sociocat.utils.YesNoDialog;

public class RegisterStep2_View extends BaseView implements
        iRegisterStep2.View,
        Validator.ValidationListener
{
    @BindView(R.id.userMessage) TextView userMessage;
    @BindView(R.id.userNameContainer) ViewGroup userNameContainer;
    @BindView(R.id.nameThrobber) ProgressBar nameThrobber;
    @BindView(R.id.saveButton) Button saveButton;
    @BindView(R.id.cancelButton) Button cancelButton;

    @NotEmpty(messageResId = R.string.cannot_be_empty)
    @BindView(R.id.userNameInput) EditText userNameInput;

    @Password(messageResId = R.string.incorrect_password)
    @Length(min = Config.PASSWORD_MIN_LENGTH, messageResId = R.string.REGISTER2_password_is_too_short)
    @BindView(R.id.password1Input) EditText password1Input;

    @ConfirmPassword(messageResId = R.string.REGISTER2_passwords_mismatch)
    @BindView(R.id.password2Input) EditText password2Input;

    private iRegisterStep2.Presenter presenter;
    private Validator validator;


    // Системные методы
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register2_activity);
        ButterKnife.bind(this);

        setPageTitle(R.string.REGISTER2_page_title);
        activateUpButton();

        RegisterStep2_ViewModel viewModel = new ViewModelProvider(this, new RegisterStep2_ViewModelFactory()).get(RegisterStep2_ViewModel.class);

        if (viewModel.hasPresenter())
            presenter = viewModel.getPresenter();
        else {
            presenter = new RegisterStep2_Presenter();
            viewModel.storePresenter(presenter);
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
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.unlinkView();
    }

    @Override
    public void onUserLogin() {

    }

    @Override
    public void onUserLogout() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                presenter.onCancelRequested();
                break;

            default:
                super.onOptionsItemSelected(item);
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        presenter.onCancelRequested();
    }


    // iRegisterStep2.View
    @Override
    public void hideUserMessage() {
        MyUtils.hide(userMessage);
    }

    @Override
    public void setState(iRegisterStep2.ViewState state, int messageId) {
        setState(state, messageId, null);
    }

    @Override
    public void setState(iRegisterStep2.ViewState state, int messageId, @Nullable String messageDetails) {

        presenter.storeViewState(state, messageId, messageDetails);

        switch (state) {
            case INITIAL:
                hideProgressMessage();
                showForm();
                showInstructions();
                break;

            case CHECKING_USER_NAME:
                disableForm();
                hideInstructions();
                hideProgressMessage();
                showNameThrobber();
                break;

            case PROGRESS:
                disableForm();
                hideInstructions();
                hideNameThrobber();
                showProgressMessage(messageId);
                break;

            case SUCCESS:
                hideProgressMessage();
                hideNameThrobber();
                showToast(messageId);
                goToMainPage();
                break;

            case NAME_ERROR:
                hideNameThrobber();
                hideProgressMessage();
                enableForm();
                showInstructions();
                userNameInput.setError(getString(messageId));
                break;

            case ERROR:
                hideNameThrobber();
                enableForm();
                showInstructions();
                showErrorMsg(messageId, messageDetails);
                break;
        }
    }

    @Override
    public String getUserName() {
        return userNameInput.getText().toString();
    }

    @Override
    public String getPassword1() {
        return password1Input.getText().toString();
    }

    @Override
    public String getPassword2() {
        return password2Input.getText().toString();
    }

    @Override
    public void showUserNameError(int msgId) {
        String message = getResources().getString(msgId);
        userNameInput.setError(message);
    }

    @Override
    public void showPassword1Error(int msgId) {
        String message = getResources().getString(msgId);
        password1Input.setError(message);
    }

    @Override
    public void showPassword2Error(int msgId) {
        String message = getResources().getString(msgId);
        password2Input.setError(message);
    }

    @Override
    public void showForm() {
        MyUtils.show(userMessage);
        MyUtils.show(userNameContainer);
        MyUtils.show(password1Input);
        MyUtils.show(password2Input);
        MyUtils.show(saveButton);
        MyUtils.show(cancelButton);
        enableForm();
    }

    @Override
    public void disableForm() {
        MyUtils.disable(password1Input);
        MyUtils.disable(password2Input);
        MyUtils.disable(saveButton);
    }

    @Override
    public void enableForm() {
        MyUtils.enable(password1Input);
        MyUtils.enable(password2Input);
        MyUtils.enable(saveButton);
    }

    @Override
    public void showNameThrobber() {
        MyUtils.disable(userNameInput);
        MyUtils.show(nameThrobber);
    }

    @Override
    public void hideNameThrobber() {
        MyUtils.enable(userNameInput);
        MyUtils.hide(nameThrobber);
    }

    @Override
    public void confirmPageLeave() {
        YesNoDialog yesNoDialog = new YesNoDialog(
                this,
                R.string.REGISTER2_leave_page_title,
                R.string.REGISTER2_leave_page_message,
                new iDialogCallbacks.YesNoCallbacks() {
                    @Override
                    public boolean onCheck() {
                        return true;
                    }

                    @Override
                    public void onYesAnswer() {
                        goToMainPage();
                    }

                    @Override
                    public void onNoAnswer() {

                    }
                }
        );

        yesNoDialog.show();
    }

    @Override
    public void setUserName(String name) {
        userNameInput.setText(name);
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
    @OnClick(R.id.saveButton)
    void onSendButtonClicked() {
        validator.validate();
    }

    @OnClick(R.id.cancelButton)
    void onCancelButtonClicked() {
        presenter.onCancelRequested();
    }


    // Закрытые методы
    private void hideInstructions() {
        MyUtils.hide(userMessage, true);
    }

    private void showInstructions() {
        MyUtils.show(userMessage);
    }
}
