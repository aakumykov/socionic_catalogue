package ru.aakumykov.me.sociocat.reset_password_step1;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.reset_password_step1.view_model.ResetPasswordStep1_ViewModel;
import ru.aakumykov.me.sociocat.reset_password_step1.view_model.ResetPasswordStep1_ViewModelFactory;
import ru.aakumykov.me.sociocat.utils.MyUtils;
import ru.aakumykov.me.sociocat.utils.my_dialogs.MyDialogs;
import ru.aakumykov.me.sociocat.utils.my_dialogs.iMyDialogs;
import ru.aakumykov.me.sociocat.z_base_view.BaseView;


public class ResetPasswordStep1_View extends BaseView implements
        iResetPasswordStep1.View,
        Validator.ValidationListener
{
    @Email(messageResId = R.string.RESET_PASSWORD_incorrect_email)
    @BindView(R.id.emailView) EditText emailInput;
    @BindView(R.id.emailThrobber) ProgressBar emailThrobber;
    @BindView(R.id.registerButton) Button sendButton;

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

        ResetPasswordStep1_ViewModel viewModel =
                new ViewModelProvider(this, new ResetPasswordStep1_ViewModelFactory())
                        .get(ResetPasswordStep1_ViewModel.class);

        if (viewModel.hasPresenter())
            presenter = viewModel.getPresenter();
        else {
            presenter = new ResetPasswordStep1_Presenter();
            viewModel.storePresenter(presenter);
        }

        validator = new Validator(this);
        validator.setValidationListener(this);
    }

    @Override public void onUserGloballyLoggedIn() {

    }
    @Override public void onUserGloballyLoggedOut() {

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
        return true;
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


    // Интерфейсные методы
    @Override
    public String getEmail() {
        return emailInput.getText().toString();
    }

    @Override
    public void finishWork() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void disableForm() {
        MyUtils.disable(emailInput);
        MyUtils.disable(sendButton);
    }

    @Override
    public void enableForm() {
        MyUtils.enable(emailInput);
        MyUtils.enable(sendButton);
    }

    @Override
    public void showEmailThrobber() {
        MyUtils.show(emailThrobber);
    }

    @Override
    public void hideEmailThrobber() {
        MyUtils.hide(emailThrobber);
    }

    @Override
    public void setState(iResetPasswordStep1.ViewState state, int messageId) {
        setState(state, messageId, null);
    }

    @Override
    public void setState(iResetPasswordStep1.ViewState state, int messageId, @Nullable String messageDetails) {

        presenter.storeViewState(state, messageId, messageDetails);

        switch (state) {
            case INITIAL:
                enableForm();
                hideProgressMessage();
                hideEmailError();
                hideEmailThrobber();
                break;

            case PROGRESS:
                disableForm();
                hideEmailThrobber();
                showProgressMessage(messageId);
                break;

            case CHECKING_EMAIL:
                disableForm();
                hideProgressMessage();
                showInfoMsg(messageId);
                showEmailThrobber();
                break;

            case SUCCESS:
                hideProgressMessage();
                hideEmailError();
                hideEmailThrobber();
                showFinishDialog();
                break;

            case COMMON_ERROR:
                enableForm();
                hideEmailError();
                hideEmailThrobber();
                showErrorMsg(messageId, messageDetails);
                break;

            case EMAIL_ERROR:
                enableForm();
                hideProgressMessage();
                hideEmailThrobber();
                showEmailError(messageId);
                break;
        }
    }

    private void showFinishDialog() {
        MyDialogs.infoDialog(
                this,
                R.string.RESET_PASSWORD_finish_dialog_title,
                R.string.RESET_PASSWORD_finish_dialog_message,
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
                        finishWork();
                    }
                }
        );
    }

    @Override
    public void showEmailError(int messageId) {
        String msg = getString(messageId);
        emailInput.setError(msg);
    }

    @Override
    public void hideEmailError() {
        emailInput.setError(null);
    }


    // Нажатия
    @OnClick(R.id.registerButton)
    void sendEmailClicked() {
        validator.validate();
    }
}
