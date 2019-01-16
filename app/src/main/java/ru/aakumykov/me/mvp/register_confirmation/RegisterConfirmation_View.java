package ru.aakumykov.me.mvp.register_confirmation;

import android.content.Intent;
import android.os.Bundle;
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
        activateUpButton();

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


    // Интерфейсные методы
    @Override
    public void showNeedsConfirmationMessage() {
        textView.setText(R.string.REGISTER_CONFIRMATION_check_your_email);
        showOkButton(R.string.REGISTER_CONFIRMATION_ok);
    }

    @Override
    public void showConfirmationSuccessMessage() {
        textView.setText(R.string.REGISTER_CONFIRMATION_registration_confirm_success);
        showOkButton(R.string.REGISTER_CONFIRMATION_ok);
    }

    @Override
    public void showConfirmationErrorMessage() {
        textView.setText(R.string.REGISTER_CONFIRMATION_registration_confirm_error);
        showOkButton(R.string.REGISTER_CONFIRMATION_continue);
    }

    @Override
    public void showEmailConfirmNotification() {
        textView.setText(R.string.REGISTER_CONFIRMATION_you_need_to_confirm_email);
        sendLetterButton.setText(R.string.REGISTER_CONFIRMATION_send_letter_again);
        MyUtils.show(sendLetterButton);
    }

    public void goMainPage(){
        Intent intent = new Intent(this, CardsGrid_View.class);
        startActivity(intent);
    }

    public void showOkButton() {
        MyUtils.show(okButton);
    }

    public void showLeaveButton() {
        MyUtils.show(leavePageButton);
    }

    public void hideLeaveButton() {
        MyUtils.hide(leavePageButton);
    }

    public void hideSendButton() {
        MyUtils.hide(sendLetterButton);
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
}
