package ru.aakumykov.me.sociocat.TEMPLATES.simple_page;

import android.content.Intent;
import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;

public class SimplePage_Presenter implements iSimplePage.Presenter {

    private iSimplePage.View view;

    @Override
    public void processInputIntent(@Nullable Intent intent) {
        if (null != intent) {
            String action = intent.getAction() + "";
            switch (action) {
                case Constants.ACTION_REGISTRATION_CONFIRM_REQUEST:
                    break;
                case Constants.ACTION_REGISTRATION_CONFIRM_RESPONSE:
                    break;
                default:
                    view.showErrorMsg(R.string.unknown_intent_action);
            }
        }
    }

    @Override
    public void linkView(iSimplePage.View view) {
        this.view = view;
    }

    @Override
    public void unlinkView() {
        this.view = null;
    }
}
