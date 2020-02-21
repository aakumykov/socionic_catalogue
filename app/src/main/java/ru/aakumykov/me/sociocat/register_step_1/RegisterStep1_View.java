package ru.aakumykov.me.sociocat.register_step_1;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.aakumykov.me.sociocat.BaseView;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.cards_grid.CardsGrid_View;
import ru.aakumykov.me.sociocat.interfaces.iMyDialogs;
import ru.aakumykov.me.sociocat.register_step_1.view_model.RegisterStep1_ViewModel;
import ru.aakumykov.me.sociocat.register_step_1.view_model.RegisterStep1_ViewModelFactory;
import ru.aakumykov.me.sociocat.utils.MyDialogs;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class RegisterStep1_View extends BaseView implements iRegisterStep1.View {

    @BindView(R.id.emailView) EditText emailInput;
    @BindView(R.id.emailThrobber) ProgressBar emailThrobber;
    @BindView(R.id.registerButton) Button sendButton;

    private iRegisterStep1.Presenter presenter;


    // Системные методы
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register1_activity);
        ButterKnife.bind(this);

        setPageTitle(R.string.REGISTER1_page_title);
        activateUpButton();

        RegisterStep1_ViewModel viewModel = new ViewModelProvider(this, new RegisterStep1_ViewModelFactory())
                .get(RegisterStep1_ViewModel.class);
        if (viewModel.hasPresenter())
            presenter = viewModel.getPresenter();
        else {
            presenter = new RegisterStep1_Presenter();
            viewModel.storePresenter(presenter);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        presenter.linkView(this);

        if (presenter.isVirgin())
            presenter.doInitialCheck();
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


    // Интерфейсные методы
    @Override
    public String getEmail() {
        return emailInput.getText().toString();
    }

    @Override
    public void showSuccessDialog() {
        MyDialogs.registrationCompleteDialog(this, new iMyDialogs.StandardCallbacks() {
            @Override
            public void onCancelInDialog() {
                goMainPage();
            }

            @Override
            public void onNoInDialog() {
                goMainPage();
            }

            @Override
            public boolean onCheckInDialog() {
                return true;
            }

            @Override
            public void onYesInDialog() {
                goMainPage();
            }
        });
    }

    @Override
    public void accessDenied(int msgId) {
        String message = getString(msgId);
        showToast(message);
        finish();
    }

    @Override
    public void setState(iRegisterStep1.ViewStates status, int messageId) {
        String emailErrorMsg = getString(messageId);
        setState(status, messageId, null);
    }

    @Override
    public void setState(iRegisterStep1.ViewStates status, int messageId, String messageDetails) {

        presenter.storeViewStatus(status, messageId, messageDetails);

        switch (status) {
            case INITIAL:
                enableForm();
                hideEmailError();
                hideEmailThrobber();
                hideProgressMessage();
                break;

            case PROGRESS:
                disableForm();
                hideEmailThrobber();
                showProgressMessage(messageId);
                break;

            case CHECKING:
                disableForm();
                hideEmailError();
                showEmailThrobber();
                //showInfoMsg(R.string.REGISTER1_checking_email);
                break;

            case EMAIL_ERROR:
                enableForm();
                hideEmailThrobber();
                hideMessage();
                showEmailError(messageId);
                break;

            case SUCCESS:
                hideEmailThrobber();
                showSuccessDialog();
                break;

            case COMMON_ERROR:
                enableForm();
                hideEmailThrobber();
                hideEmailError();
                if (null != messageDetails) showErrorMsg(R.string.error, messageDetails, true);
                break;

            default:
                break;
        }
    }


    // Нажатия
    @OnClick(R.id.registerButton)
    void onRegisterButtonClicked() {
        presenter.onRegisterButtonClicked();
    }

    @OnClick(R.id.cancelButton)
    void onCancelButtonClicked() {
        closePage();
    }


    // Внутренние методы
    private void disableForm() {
        MyUtils.disable(emailInput);
        MyUtils.disable(sendButton);
    }

    private void enableForm() {
        MyUtils.enable(emailInput);
        MyUtils.enable(sendButton);
    }

    private void showEmailThrobber() {
        MyUtils.disable(emailInput);
        MyUtils.show(emailThrobber);
    }

    private void hideEmailThrobber() {
        MyUtils.enable(emailInput);
        MyUtils.hide(emailThrobber);
    }

    private void showEmailError(int messageId) {
        emailInput.setError(getString(messageId));
    }

    private void hideEmailError() {
        emailInput.setError(null);
    }

    void goMainPage() {
        Intent intent = new Intent(this, CardsGrid_View.class);
        startActivity(intent);
    }
}
