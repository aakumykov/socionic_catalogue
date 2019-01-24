package ru.aakumykov.me.mvp.register2;

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
import ru.aakumykov.me.mvp.utils.MyUtils;

public class Register2_View extends BaseView implements iRegister2.View {

    @BindView(R.id.password1Input) EditText password1Input;
    @BindView(R.id.password2Input) EditText password2Input;
    @BindView(R.id.saveButton) Button saveButton;

    private iRegister2.Presenter presenter;
    private boolean firstRun = true;

    // Системные методы
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register2_activity);
        ButterKnife.bind(this);

        setPageTitle(R.string.REGISTER2_page_title);
        activateUpButton();

        presenter = new Register2_Presenter();
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
    public String getPassword1() {
        return null;
    }

    @Override
    public String getPassword2() {
        return null;
    }

    @Override
    public void showPassword1Error(int msgId) {
        String message = getResources().getString(msgId);
        password1Input.setError(message);
    }

    @Override
    public void showPassword2Error(int msgId) {
        String message = getResources().getString(msgId);
        password2Input.setError(message);
    }

    @Override
    public void disableForm() {
        MyUtils.disable(password1Input);
        MyUtils.disable(password2Input);
        MyUtils.disable(saveButton);
    }

    @Override
    public void enableForm() {
        MyUtils.enable(password1Input);
        MyUtils.enable(password2Input);
        MyUtils.enable(saveButton);
    }

    @Override
    public void goMainPage() {
        Intent intent = new Intent(this, CardsGrid_View.class);
        startActivity(intent);
    }


    // Нажатия
    @OnClick(R.id.sendButton)
    void onSendButtonClicked() {
        presenter.finishRegistration(getIntent());
    }

}
