package ru.aakumykov.me.sociocat.user_change_password;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Order;
import com.mobsandgeeks.saripaar.annotation.Password;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.user_change_password.view_model.UserChangePassword_ViewModel;
import ru.aakumykov.me.sociocat.user_change_password.view_model.UserChangePassword_ViewModelFactory;
import ru.aakumykov.me.sociocat.utils.MyUtils;
import ru.aakumykov.me.sociocat.z_base_view.BaseView;

public class UserChangePassword_View extends BaseView implements
        iUserChangePassword.iView,
        Validator.ValidationListener
{
    @Order(1)
    @NotEmpty(messageResId = R.string.cannot_be_empty)
    @BindView(R.id.currentPasswordInput)
    EditText currentPasswordInput;

    @Order(2)
    @Length(min = 6, messageResId = R.string.USER_CHANGE_PASSWORD_wrong_password_length)
    @Password(messageResId = R.string.USER_CHANGE_PASSWORD_passwords_mismatch)
    @BindView(R.id.newPasswordInput)
    EditText newPasswordInput;

    @Order(2)
    @ConfirmPassword(messageResId = R.string.USER_CHANGE_PASSWORD_passwords_mismatch)
    @BindView(R.id.newPasswordConfirmationInput)
    EditText newPasswordConfirmationInput;

    @BindView(R.id.saveButton)
    Button saveButton;

    @BindView(R.id.cancelButton)
    Button cancelButton;

    private iUserChangePassword.iPresenter presenter;
    private Validator validator;


    // Activity
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_change_password_activity);
        ButterKnife.bind(this);

        setPageTitle(R.string.USER_CHANGE_PASSWORD_page_title);
        activateUpButton();

        UserChangePassword_ViewModel viewModel = new ViewModelProvider(this, new UserChangePassword_ViewModelFactory())
                .get(UserChangePassword_ViewModel.class);

        if (viewModel.hasPresenter()) {
            this.presenter = viewModel.getPresenter();
        } else {
            this.presenter = new UserChangePassword_Presenter();
            viewModel.storePresenter(this.presenter);
        }

        validator = new Validator(this);
        validator.setValidationListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
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


    // BaseView
    @Override
    public void onUserGloballyLoggedIn() {

    }

    @Override
    public void onUserGloballyLoggedOut() {
        presenter.onUserLoggedOut();
    }


    // iUserChangePassword.iView
    @Override
    public void setState(iUserChangePassword.ViewState state, int messageId) {
        setState(state, messageId, null);
    }

    @Override
    public void setState(iUserChangePassword.ViewState state, int messageId, @Nullable String messageDetails) {

        presenter.storeViewState(state, messageId, messageDetails);

        switch (state) {
            case PROGRESS:
                disableForm();
                showProgressMessage(messageId);
                break;

            case SUCCESS:
                hideProgressMessage();
                showToast(messageId);
                closePage();
                break;

            case ERROR:
                enableForm();
                showErrorMsg(messageId, messageDetails);
                break;

/*            case CURRENT_PASSWORD_ERROR:
                break;

            case NEW_PASSWORD_ERROR:
                break;*/
        }
    }

    @Override
    public String getCurrentPassword() {
        return currentPasswordInput.getText().toString();
    }

    @Override
    public String getNewPassword() {
        return newPasswordInput.getText().toString();
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
    void onSaveButtonClicked() {
        validator.setValidationMode(Validator.Mode.IMMEDIATE);
        validator.validate();
    }

    @OnClick(R.id.cancelButton)
    void onCancelButtonClicked() {
        presenter.onCancelButtonClicked();
    }


    // Внутренние методы
    private void enableForm() {
        MyUtils.enable(currentPasswordInput);
        MyUtils.enable(newPasswordInput);
        MyUtils.enable(newPasswordConfirmationInput);
        MyUtils.enable(saveButton);
    }

    private void disableForm() {
        MyUtils.disable(currentPasswordInput);
        MyUtils.disable(newPasswordInput);
        MyUtils.disable(newPasswordConfirmationInput);
        MyUtils.disable(saveButton);
    }
}
