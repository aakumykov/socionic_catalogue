package ru.aakumykov.me.mvp.register_confirmation;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.aakumykov.me.mvp.BaseView;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.cards_grid.CardsGrid_View;
import ru.aakumykov.me.mvp.utils.MyUtils;

public class RegisterConfirmation_View extends BaseView implements
        iRegisterConfirmation.View
{
    @BindView(R.id.textView) TextView textView;
    @BindView(R.id.okButton) Button okButton;
    @BindView(R.id.sendLetterButton) Button sendLetterButton;
    @BindView(R.id.leavePageButton) Button leavePageButton;
    private iRegisterConfirmation.Presenter presenter;


    // Системные методы
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_confirmation_activity);
        ButterKnife.bind(this);

        setPageTitle(R.string.REGISTER2_email_confirmation);
//        activateUpButton();

        presenter = new RegisterConfirmation_Presenter();
    }

    @Override
    public void onUserLogin() {

    }

    @Override
    public void onUserLogout() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.linkView(this);
        presenter.processInputIntent(getIntent());
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.unlinkView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }


    // Интерфейсные методы
    @Override
    public void showEmailNeedsConfirmation(String email) {
        String msg = getResources().getString(R.string.REGISTER_CONFIRMATION_check_your_email, email);
        textView.setText(msg);
        showOkButton(R.string.REGISTER_CONFIRMATION_ok);
    }

    @Override
    public void showConfirmationSuccess() {
        textView.setText(R.string.REGISTER_CONFIRMATION_registration_confirm_success);
        showOkButton(R.string.REGISTER_CONFIRMATION_ok);
    }

    @Override
    public void showConfirmationError() {
        showErrorMsg(R.string.REGISTER_CONFIRMATION_registration_confirm_error);
        MyUtils.show(sendLetterButton);
        MyUtils.show(leavePageButton);
    }


    @Override
    public void notifyEmailNeedsConfirmation() {
        textView.setText(R.string.REGISTER_CONFIRMATION_you_need_to_confirm_email);
        sendLetterButton.setText(R.string.REGISTER_CONFIRMATION_send_letter_again);
        MyUtils.show(sendLetterButton);
        MyUtils.show(leavePageButton);
    }

    @Override
    public void showEmailSending() {
        showProgressMessage(R.string.REGISTER_CONFIRMATION_sending_confirmation_message);
        MyUtils.hide(sendLetterButton);
        MyUtils.hide(leavePageButton);
    }

    @Override
    public void showEmailSendSuccess() {
        showToast(R.string.REGISTER_CONFIRMATION_email_sended);
        goMainPage();
    }

    @Override
    public void showEmailSendError() {
        showErrorMsg(R.string.REGISTER_CONFIRMATION_error_sending_email);
        MyUtils.show(sendLetterButton);
        MyUtils.show(leavePageButton);
    }


    // Нажатия
    @OnClick(R.id.okButton)
    void okButtonClicked() {
        Intent intent = new Intent(this, CardsGrid_View.class);
        startActivity(intent);
    }

    @OnClick(R.id.sendLetterButton)
    void sendButtonClicked() {
        presenter.sendEmailConfirmation();
    }

    @OnClick(R.id.leavePageButton)
    void leaveButtonClicked(){
        finish();
    }


    // Внутренние методы
    private void showOkButton(int messageId) {
        okButton.setText(getResources().getString(messageId));
        MyUtils.show(okButton);
    }

    private void goMainPage(){
        Intent intent = new Intent(this, CardsGrid_View.class);
        startActivity(intent);
    }

}
