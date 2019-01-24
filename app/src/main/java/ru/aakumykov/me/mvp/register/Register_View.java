package ru.aakumykov.me.mvp.register;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.aakumykov.me.mvp.BaseView;
import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.cards_grid.CardsGrid_View;
import ru.aakumykov.me.mvp.interfaces.iMyDialogs;
import ru.aakumykov.me.mvp.register_confirmation.RegisterConfirmation_View;
import ru.aakumykov.me.mvp.utils.MyDialogs;
import ru.aakumykov.me.mvp.utils.MyUtils;

public class Register_View extends BaseView implements
    iRegister.View
{
    @BindView(R.id.nameThrobber) ProgressBar nameThrobber;
    @BindView(R.id.nameInput) EditText nameInput;

    @BindView(R.id.emailThrobber) ProgressBar emailThrobber;
    @BindView(R.id.emailInput) EditText emailInput;

    @BindView(R.id.password1Input) EditText password1Input;
    @BindView(R.id.password2Input) EditText password2Input;

    @BindView(R.id.registerButton) Button registerButton;
    @BindView(R.id.cancelButton) Button cancelButton;

    private iRegister.Presenter presenter;
    private boolean firstRun = true;


    // Системные методы
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        ButterKnife.bind(this);

        if (auth().isUserLoggedIn()) {
            showToast(R.string.REGISTER2_you_are_already_registered);
            finish();
        }

        setPageTitle(R.string.REGISTER2_page_title);
        activateUpButton();

        presenter = new Register_Presenter();
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.linkView(this);
        if (firstRun) {
            firstRun = false;
        }
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
    public String getName() {
        return nameInput.getText().toString();
    }

    @Override
    public String getEmail() {
        return emailInput.getText().toString();
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
    public void showNameError(int messageId) {
        showInputError(nameInput, messageId);
    }

    @Override
    public void showEmailError(int messageId) {
        showInputError(emailInput, messageId);
    }

    @Override
    public void showPassword1Error(int messageId) {
        showInputError(password1Input, messageId);
    }

    @Override
    public void showPassword2Error(int messageId) {
        showInputError(password2Input, messageId);
    }

    @Override
    public void disableNameInput() {
        MyUtils.disable(nameInput);
        MyUtils.show(nameThrobber);
    }

    @Override
    public void enableNameInput() {
        MyUtils.enable(nameInput);
        MyUtils.hide(nameThrobber);
    }

    @Override
    public void disableEmailInput() {
        MyUtils.disable(emailInput);
        MyUtils.show(emailThrobber);
    }

    @Override
    public void enableEmailInput() {
        MyUtils.enable(emailInput);
        MyUtils.hide(emailThrobber);
    }

    @Override
    public void disableForm() {
        MyUtils.disable(nameInput);
        MyUtils.disable(emailInput);
        MyUtils.disable(password1Input);
        MyUtils.disable(password2Input);
        MyUtils.disable(registerButton);
    }

    @Override
    public void enableForm() {
        MyUtils.enable(nameInput);
        MyUtils.enable(emailInput);
        MyUtils.enable(password1Input);
        MyUtils.enable(password2Input);
        MyUtils.enable(registerButton);
    }

    @Override
    public void finishRegistration(String email) {

        MyDialogs.registrationCompleteDialog(this, new iMyDialogs.StandardCallbacks() {
            @Override
            public void onCancelInDialog() {
                goHomePage();
            }

            @Override
            public void onNoInDialog() {

            }

            @Override
            public boolean onCheckInDialog() {
                return true;
            }

            @Override
            public void onYesInDialog() {
                goHomePage();
            }
        });


    }


    // Нажатия
    @OnClick(R.id.registerButton)
    public void register() {
        presenter.registerUser();
    }

    @OnClick(R.id.cancelButton)
    void cancelRegistration() {
        finish();
    }


    // Внутренние методы
    private <T> void showInputError(EditText textInput, T  msg) {
        String errorMsg = "";
        if (msg instanceof Integer) errorMsg = getResources().getString((Integer)msg);
        else errorMsg = String.valueOf(msg);

        textInput.requestFocus();
        textInput.setError(errorMsg);
    }

    private void goHomePage() {
        Intent intent = new Intent(this, CardsGrid_View.class);
        startActivity(intent);
    }
}
