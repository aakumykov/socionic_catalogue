package ru.aakumykov.me.mvp.register_confirmation;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.interfaces.iUsersSingleton;
import ru.aakumykov.me.mvp.services.UsersSingleton;

public class RegisterConfirmation_Presenter implements iRegisterConfirmation.Presenter {

    private iRegisterConfirmation.View view;
    private iUsersSingleton usersService = UsersSingleton.getInstance();
    private String userId;

    // Системныя методы
    @Override
    public void linkView(iRegisterConfirmation.View view) {
        this.view = view;
    }

    @Override
    public void unlinkView() {
        this.view = null;
    }


    // Интерфейсныя методы
    @Override
    public void processInputIntent(@Nullable Intent intent) {

        if (null != intent) {

            String action = intent.getAction() + "";

            switch (action) {

                case Constants.ACTION_REGISTRATION_CONFIRM_REQUEST:
                    view.showNeedsConfirmationMessage();
                    break;

                case Constants.ACTION_REGISTRATION_CONFIRM_RESPONSE:
                    processEmailConfirmation();
                    break;

                case Constants.ACTION_REGISTRATION_CONFIRM_NOTIFICATION:
                    processEmailConfirmationNotification(intent);
                    break;

                default:
                    view.showErrorMsg(R.string.unknown_intent_action);
            }
        }
    }

    @Override
    public void sendEmailConfirmation() {

        view.hideMsg();
        view.hideLeaveButton();
        view.showProgressMessage(R.string.REGISTER_CONFIRMATION_sending_confirmation_message);

        usersService.sendEmailVerificationLink(Constants.PACKAGE_NAME, new iUsersSingleton.SendEmailVerificationLinkCallbacks() {
            @Override
            public void onEmailVerificationLinkSendSuccess() {
                view.hideProgressBar();
                view.showInfoMsg(R.string.REGISTER_CONFIRMATION_email_sended);
                view.showOkButton();
            }

            @Override
            public void onEmailVerificationLinkSendFail(String errorMsg) {
                view.showErrorMsg(R.string.REGISTER_CONFIRMATION_error_sending_email, errorMsg);
                view.showLeaveButton();
            }
        });
    }


    // Внутренния методы
    private void processEmailConfirmation() {

    }

    private void processEmailConfirmationNotification(@NonNull Intent intent) {
        String userId = intent.getStringExtra(Constants.USER_ID);
        if (null != userId) {
            this.userId = userId;
        }
    }
}
