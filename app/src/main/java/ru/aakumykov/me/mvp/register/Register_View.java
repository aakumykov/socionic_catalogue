package ru.aakumykov.me.mvp.register;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
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
import ru.aakumykov.me.mvp.users.show.UserShow_View;

// TODO: no-history

public class Register_View extends BaseView implements
        iRegister.View
{
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
    public void disableForm() {
        MyUtils.disable(emailInput);
        MyUtils.disable(passwordInput1);
        MyUtils.disable(passwordInput2);
        MyUtils.disable(registerButton);
    }

    @Override
    public void enableForm() {
        MyUtils.enable(emailInput);
        MyUtils.enable(passwordInput1);
        MyUtils.enable(passwordInput2);
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
        Log.d(TAG, "register()");

        String email = emailInput.getText().toString();
        String password1 = passwordInput1.getText().toString();
        String password2 = passwordInput2.getText().toString();

        if (password1.equals(password2)) {
            showProgressBar();
            showInfoMsg(R.string.REGISTER_registering_user);
            disableForm();

            try {
                presenter.regUserWithEmail(email, password1);
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

    // TODO: как _реально_ прервать рагистрацию, чтобы не создавать фантомных пользователей?
    @OnClick(R.id.cancelButton)
    void cancelRegister() {
        closePage();
    }
}
