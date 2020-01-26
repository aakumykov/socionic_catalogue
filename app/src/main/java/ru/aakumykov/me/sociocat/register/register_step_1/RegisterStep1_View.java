package ru.aakumykov.me.sociocat.register.register_step_1;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.aakumykov.me.sociocat.BaseView;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.cards_grid.CardsGrid_View;
import ru.aakumykov.me.sociocat.interfaces.iMyDialogs;
import ru.aakumykov.me.sociocat.utils.MyDialogs;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class RegisterStep1_View extends BaseView implements iRegisterStep1.View {

    @BindView(R.id.emailView) EditText emailInput;
    @BindView(R.id.emailThrobber) ProgressBar emailThrobber;
    @BindView(R.id.sendButton) Button sendButton;

    private iRegisterStep1.Presenter presenter;


    // Системные методы
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register1_activity);
        ButterKnife.bind(this);

        setPageTitle(R.string.REGISTER1_page_title);
        activateUpButton();

        presenter = new RegisterStep1_Presenter();
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.linkView(this);
        presenter.doInitialCheck();
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
    public void showEmailThrobber() {
        MyUtils.disable(emailInput);
        MyUtils.show(emailThrobber);
    }

    @Override
    public void hideEmailThrobber() {
        MyUtils.enable(emailInput);
        MyUtils.hide(emailThrobber);
    }

    @Override
    public String getEmail() {
        return emailInput.getText().toString();
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
    public void showEmailError(String message) {
        emailInput.setError(message);
    }

    @Override
    public void hideEmailError() {
        emailInput.setError(null);
    }

    @Override
    public void accessDenied(int msgId) {
        String message = getString(msgId);
        showToast(message);
        finish();
    }

    @Override
    public void setStatus(iRegisterStep1.ViewStatus status, int errorMessageId) {
        String emailErrorMsg = getString(errorMessageId);
        setStatus(status, emailErrorMsg);
    }

    @Override
    public void setStatus(iRegisterStep1.ViewStatus status, String errorMessage) {
        switch (status) {
            case CHECKING:
                disableForm();
                hideEmailError();
                showEmailThrobber();
                showInfoMsg(R.string.REGISTER1_checking_email);
                break;

            case EMAIL_ERROR:
                enableForm();
                hideEmailThrobber();
                hideMessage();
                if (null != errorMessage) showEmailError(errorMessage);
                break;

            case SUCCESS:
                break;

            case COMMON_ERROR:
                enableForm();
                hideEmailThrobber();
                hideEmailError();
                if (null != errorMessage) showErrorMsg(R.string.error, errorMessage, true);
                break;

            default:
                throw new RuntimeException("Unknown status: "+status);
        }
    }


    // Нажатия
    @OnClick(R.id.sendButton)
    void onSendButtonClicked() {
        presenter.produceRegistrationStep1();
    }


    // Внутренние методы
    void goMainPage() {
        Intent intent = new Intent(this, CardsGrid_View.class);
        startActivity(intent);
    }
}
