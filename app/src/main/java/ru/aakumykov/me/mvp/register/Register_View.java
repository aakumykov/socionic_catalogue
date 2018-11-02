package ru.aakumykov.me.mvp.register;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
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
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.messageView) TextView messageView;
//    @BindView(R.id.nameInput) EditText nameInput;
//    @BindView(R.id.emailInput) EditText emailInput;
//    @BindView(R.id.passwordInput1) EditText passwordInput1;
//    @BindView(R.id.passwordInput2) EditText passwordInput2;

    private final static String TAG = "Register_View";
    private iRegister.Presenter presenter;

    // Системные методы
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qwerty);
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


    // Обработчики нажатий
    @OnClick(R.id.registerButton)
    void register() {
//        Log.d(TAG, "register()");

//        showProgressBar();
//        MyUtils.hide(progressBar);

        if (progressBar.getVisibility() == View.VISIBLE) {
            Log.d(TAG, "Скрываем панель");
            progressBar.setVisibility(View.INVISIBLE);
            messageView.setVisibility(View.INVISIBLE);
        }
        else {
            Log.d(TAG, "Показываем панель");
            progressBar.setVisibility(View.VISIBLE);
            messageView.setVisibility(View.VISIBLE);
            if (progressBar.getVisibility() != View.VISIBLE) Log.d(TAG, "НО ОНА НЕ СТАЛА ВИДИМОЙ");
        }

        showInfoMsg(R.string.REGISTER_registering_user);

//        String name = nameInput.getText().toString();
//        String email = emailInput.getText().toString();
//        String password1 = passwordInput1.getText().toString();
//        String password2 = passwordInput2.getText().toString();
//
//        if (password1.equals(password2)) {
//            presenter.regUserWithEmail(name, email, password1);
//        } else {
//            showErrorMsg(R.string.REGISTER_passwords_mismatch);
//        }
    }


    // Коллбеки

}
