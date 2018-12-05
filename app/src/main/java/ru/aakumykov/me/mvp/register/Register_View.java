package ru.aakumykov.me.mvp.register;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.aakumykov.me.mvp.BaseView;
import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.users.edit.UserEdit_View;
import ru.aakumykov.me.mvp.utils.MyUtils;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.models.User;

// TODO: no-history

public class Register_View extends BaseView implements
        iRegister.View
{
    @BindView(R.id.emailInput) EditText emailInput;
    @BindView(R.id.passwordInput1) EditText passwordInput;
    @BindView(R.id.passwordInput2) EditText passwordConfirmationInput;
    @BindView(R.id.registerButton) Button registerButton;
    @BindView(R.id.cancelButton) Button cancelButton;

    private final static String TAG = "Register_View";
    private iRegister.Presenter presenter;

    // Системные методы
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        ButterKnife.bind(this);

        setPageTitle(R.string.REGISTER_page_title);
        activateUpButton();

        presenter = new Register_Presenter();
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


    // Обязательные методы
    @Override
    public void onUserLogin() {
        // TODO: делать здесь что-нибудь
    }

    @Override
    public void onUserLogout() {

    }


    // Интерфейсные методы
    @Override
    public String getEmail() {
        return emailInput.getText().toString();
    }

    @Override
    public String getPassword() {
        return passwordInput.getText().toString();
    }

    @Override
    public String getPasswordConfirmation() {
        return passwordConfirmationInput.getText().toString();
    }

    @Override
    public void focusEmail() {
        emailInput.requestFocus();
    }

    @Override
    public void focusPassword() {
        passwordInput.requestFocus();
    }

    @Override
    public void focusPasswordConfigmation() {
        passwordConfirmationInput.requestFocus();
    }

    @Override
    public void disableForm() {
        MyUtils.disable(emailInput);
        MyUtils.disable(passwordInput);
        MyUtils.disable(passwordConfirmationInput);
        MyUtils.disable(registerButton);
    }

    @Override
    public void enableForm() {
        MyUtils.enable(emailInput);
        MyUtils.enable(passwordInput);
        MyUtils.enable(passwordConfirmationInput);
        MyUtils.enable(registerButton);
    }

    @Override
    public void goUserEditPage(User user) {
        Intent intent = new Intent(this, UserEdit_View.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.putExtra(Constants.USER_ID, user.getKey());
        startActivity(intent);
    }


    // Обработчики нажатий
    @OnClick(R.id.registerButton)
    void register() {
        presenter.regUserWithEmail();
    }

    // TODO: как _реально_ прервать рагистрацию, чтобы не создавать фантомных пользователей?
    @OnClick(R.id.cancelButton)
    void cancelRegister() {
        closePage();
    }
}
