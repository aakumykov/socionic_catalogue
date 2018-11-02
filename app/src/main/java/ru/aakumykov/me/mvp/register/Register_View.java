package ru.aakumykov.me.mvp.register;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.aakumykov.me.mvp.BaseView;
import ru.aakumykov.me.mvp.MyUtils;
import ru.aakumykov.me.mvp.R;

public class Register_View extends BaseView implements
        iRegister.View
{
    @BindView(R.id.nameInput) EditText nameInput;
    @BindView(R.id.emailInput) EditText emailInput;
    @BindView(R.id.passwordInput1) EditText passwordInput1;
    @BindView(R.id.passwordInput2) EditText passwordInput2;
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

        setPageTitle("Register_View");

        presenter = new Register_Presenter();
    }

    @Override
    public void onServiceBounded() {
        presenter.linkView(this);
        presenter.linkModel(getCardsService());
        presenter.linkAuth(getAuthService());
    }

    @Override
    public void onServiceUnbounded() {
        presenter.unlinkView();
        presenter.unlinkModel();
        presenter.unlinkAuth();
    }


    // Интерфейсные методы


    @Override
    public void disableForm() {
        MyUtils.disable(nameInput);
        MyUtils.disable(emailInput);
        MyUtils.disable(passwordInput1);
        MyUtils.disable(passwordInput2);
        MyUtils.disable(registerButton);
    }

    @Override
    public void enableForm() {
        MyUtils.enable(nameInput);
        MyUtils.enable(emailInput);
        MyUtils.enable(passwordInput1);
        MyUtils.enable(passwordInput2);
        MyUtils.enable(registerButton);
    }

    // Обработчики нажатий
    @OnClick(R.id.registerButton)
    void register() {
        Log.d(TAG, "register()");

        String name = nameInput.getText().toString();
        String email = emailInput.getText().toString();
        String password1 = passwordInput1.getText().toString();
        String password2 = passwordInput2.getText().toString();

        if (password1.equals(password2)) {
            showProgressBar();
            showInfoMsg(R.string.REGISTER_registering_user);
            disableForm();

            try {
                presenter.regUserWithEmail(name, email, password1);
            } catch (Exception e) {
                hideProgressBar();
                enableForm();
                showErrorMsg(e.getMessage());
                e.printStackTrace();
            }

        } else {
            showErrorMsg(R.string.REGISTER_passwords_mismatch);
        }
    }


    // Коллбеки

}
