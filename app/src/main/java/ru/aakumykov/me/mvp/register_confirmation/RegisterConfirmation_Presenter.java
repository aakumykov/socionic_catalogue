package ru.aakumykov.me.mvp.register_confirmation;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.interfaces.iAuthSingleton;
import ru.aakumykov.me.mvp.interfaces.iUsersSingleton;
import ru.aakumykov.me.mvp.services.AuthSingleton;
import ru.aakumykov.me.mvp.services.UsersSingleton;

public class RegisterConfirmation_Presenter implements iRegisterConfirmation.Presenter {

    private iRegisterConfirmation.View view;
    private iUsersSingleton usersService = UsersSingleton.getInstance();
    private iAuthSingleton authService = AuthSingleton.getInstance();
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
                    notifyNeedsEmailConfirmation();
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

        authService.sendEmailVerificationLink(Constants.PACKAGE_NAME, new iAuthSingleton.SendEmailVerificationLinkCallbacks() {
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
            view.showEmailConfirmationInfo(email);
        } else {
            view.showErrorMsg(R.string.REGISTER_CONFIRMATION_error_no_required_data);
        }
    }

    private void processEmailConfirmation() {

    }

    private void notifyNeedsEmailConfirmation() {
        view.showEmailConfirmationNotification();
    }
}
