package ru.aakumykov.me.mvp.register2;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.aakumykov.me.mvp.BaseView;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.cards_grid.CardsGrid_View;
import ru.aakumykov.me.mvp.utils.MyUtils;

public class Register2_View extends BaseView implements
    iRegister2.View
{
    @BindView(R.id.nameInput) EditText nameInput;
    @BindView(R.id.emailInput) EditText emailInput;
    @BindView(R.id.password1Input) EditText password1Input;
    @BindView(R.id.password2Input) EditText password2Input;

    @BindView(R.id.nameErrorView) TextView nameErrorView;
    @BindView(R.id.emailErrorView) TextView emailErrorView;
    @BindView(R.id.passwordErrorView) TextView passwordErrorView;

    @BindView(R.id.registerButton) Button registerButton;
    @BindView(R.id.cancelButton) Button cancelButton;


    // Системные методы
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register2_activity);
        ButterKnife.bind(this);
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
    public void showNameError() {

    }

    @Override
    public void showEmailError() {

    }

    @Override
    public void showPasswordError() {

    }

    @Override
    public void finishAndGoToApp() {
        Intent intent = new Intent(this, CardsGrid_View.class);
        startActivity(intent);
    }


    // Нажатия
    @OnClick(R.id.registerButton)
    public void register() {
        showInputError(nameInput, nameErrorView, "Просто будет ошибка");
    }


    // Внутренние методы
    private <T> void showInputError(EditText textInput, TextView errorView, T  msg) {
        String errorMsg = "";
        if (msg instanceof Integer) errorMsg = getResources().getString((Integer)msg);
        else errorMsg = String.valueOf(msg);

        int errorColor = getResources().getColor(R.color.error);
        int errorBgColor = getResources().getColor(R.color.error_background);

        textInput.requestFocus();
        textInput.setError(errorMsg);

        errorView.setTextColor(errorColor);
        errorView.setBackgroundColor(errorBgColor);
        errorView.setText(errorMsg);
        MyUtils.show(errorView);
    }
}
