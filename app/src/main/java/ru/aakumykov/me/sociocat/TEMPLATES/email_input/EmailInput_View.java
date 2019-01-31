package ru.aakumykov.me.sociocat.TEMPLATES.email_input;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.aakumykov.me.sociocat.BaseView;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.cards_grid.CardsGrid_View;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class EmailInput_View extends BaseView implements iEmailInput.View {

    @BindView(R.id.emailInput) EditText emailInput;
    @BindView(R.id.sendButton) Button sendButton;

    private iEmailInput.Presenter presenter;


    // Системные методы
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.email_input_activity);
        ButterKnife.bind(this);

        setPageTitle(R.string.REGISTER1_page_title);
        activateUpButton();

        presenter = new EmailInput_Presenter();
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
    public void showEmailError(String msgId) {
        emailInput.setError(msgId);
    }



    // Нажатия
    @OnClick(R.id.sendButton)
    void onSendButtonClicked() {

    }


    // Внутренние методы
    void goMainPage() {
        Intent intent = new Intent(this, CardsGrid_View.class);
        startActivity(intent);
    }
}
