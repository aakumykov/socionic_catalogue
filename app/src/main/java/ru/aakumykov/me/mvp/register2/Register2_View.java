package ru.aakumykov.me.mvp.register2;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.aakumykov.me.mvp.BaseView;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.cards_grid.CardsGrid_View;
import ru.aakumykov.me.mvp.services.AuthSingleton;
import ru.aakumykov.me.mvp.utils.MyUtils;

public class Register2_View extends BaseView implements
    iRegister2.View
{
    @BindView(R.id.nameInput) EditText nameInput;
    @BindView(R.id.emailInput) EditText emailInput;
    @BindView(R.id.password1Input) EditText password1Input;
    @BindView(R.id.password2Input) EditText password2Input;
    @BindView(R.id.registerButton) Button registerButton;
    @BindView(R.id.cancelButton) Button cancelButton;

    private iRegister2.Presenter presenter;
    private boolean firstRun = true;

    // Системные методы
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register2_activity);
        ButterKnife.bind(this);

        setPageTitle(R.string.REGISTER2_page_title);

        presenter = new Register2_Presenter();
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
        showToast(R.string.REGISTER2_you_are_already_registered);
        Intent intent = new Intent(this, CardsGrid_View.class);
        startActivity(intent);
    }

    @Override
    public void onUserLogout() {

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
    public void showPasswordError(int messageId) {
        showInputError(password1Input, messageId);
    }

    @Override
    public void disableNameInput() {
        MyUtils.disable(nameInput);
    }

    @Override
    public void enableNameInput() {
        MyUtils.enable(nameInput);
    }

    @Override
    public void disableEmailInput() {
        MyUtils.disable(emailInput);
    }

    @Override
    public void enableEmailInput() {
        MyUtils.enable(emailInput);
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
    public void finishAndGoToApp() {
        Intent intent = new Intent(this, CardsGrid_View.class);
        startActivity(intent);
    }


    // Нажатия
    @OnClick(R.id.registerButton)
    public void register() {
        presenter.registerUser(new iRegister2.RegistrationCallbacks() {
            @Override
            public void onRegisrtationSuccess() {
                showToast(R.string.REGISTER2_succes);
                finishAndGoToApp();
            }

            @Override
            public void onRegisrtationFail(String errorMsg) {
                showErrorMsg(R.string.REGISTER2_registration_failed, errorMsg);
                enableForm();
            }
        });
    }


    // Внутренние методы
    private <T> void showInputError(EditText textInput, T  msg) {
        String errorMsg = "";
        if (msg instanceof Integer) errorMsg = getResources().getString((Integer)msg);
        else errorMsg = String.valueOf(msg);

        textInput.requestFocus();
        textInput.setError(errorMsg);
    }
}
