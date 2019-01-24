package ru.aakumykov.me.mvp.register1;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.aakumykov.me.mvp.BaseView;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.cards_grid.CardsGrid_View;
import ru.aakumykov.me.mvp.interfaces.iMyDialogs;
import ru.aakumykov.me.mvp.utils.MyDialogs;
import ru.aakumykov.me.mvp.utils.MyUtils;

public class Register1_View extends BaseView implements iRegister1.View {

    @BindView(R.id.emailInput) EditText emailInput;
    @BindView(R.id.sendButton) Button sendButton;

    private iRegister1.Presenter presenter;


    // Системные методы
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register1_activity);
        ButterKnife.bind(this);

        setPageTitle(R.string.REGISTER1_page_title);
        activateUpButton();

        presenter = new Register1_Presenter();
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
    public void showEmailError(String msgId) {
        emailInput.setError(msgId);
    }


    // Нажатия
    @OnClick(R.id.sendButton)
    void onSendButtonClicked() {
        presenter.sendRegistrationEmail();
    }


    // Внутренние методы
    void goMainPage() {
        Intent intent = new Intent(this, CardsGrid_View.class);
        startActivity(intent);
    }
}
