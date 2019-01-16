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
                    showNeedsEmailConfirmation(intent);
                    break;

                case Constants.ACTION_REGISTRATION_CONFIRM_NOTIFICATION:
                    notifyNeedsEmailConfirmation(intent);
                    break;

                case Constants.ACTION_REGISTRATION_CONFIRM_RESPONSE:
                    processEmailConfirmation();
                    break;

                default:
                    view.showErrorMsg(R.string.unknown_intent_action);
            }
        }
    }

    @Override
    public void sendEmailConfirmation() {

        view.showEmailSending();

        usersService.sendEmailVerificationLink(Constants.PACKAGE_NAME, new iUsersSingleton.SendEmailVerificationLinkCallbacks() {
            @Override
            public void onEmailVerificationLinkSendSuccess() {
                view.showEmailSendSuccess();
            }

            @Override
            public void onEmailVerificationLinkSendFail(String errorMsg) {
                view.showEmailSendError();
            }
        });
    }


    // Внутренния методы
    private void showNeedsEmailConfirmation(@NonNull Intent intent) {
        String email = intent.getStringExtra(Constants.USER_EMAIL);
        if (null != email) {
            view.showEmailNeedsConfirmation(email);
        }
    }

    private void processEmailConfirmation() {

    }

    private void notifyNeedsEmailConfirmation(@NonNull Intent intent) {
        String userId = intent.getStringExtra(Constants.USER_ID);
        if (null != userId) {
            this.userId = userId;
            view.notifyEmailNeedsConfirmation();
        }
    }
}
