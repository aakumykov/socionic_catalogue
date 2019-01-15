package ru.aakumykov.me.mvp.register_confirmation;

import android.content.Intent;
import android.support.annotation.Nullable;

import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.R;

public class RegisterConfirmation_Presenter implements iRegisterConfirmation.Presenter {

    private iRegisterConfirmation.View view;

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

                default:
                    view.showErrorMsg(R.string.unknown_intent_action);
            }
        }
    }

    @Override
    public void linkView(iRegisterConfirmation.View view) {
        this.view = view;
    }

    @Override
    public void unlinkView() {
        this.view = null;
    }


    private void processEmailConfirmation() {

    }
}
