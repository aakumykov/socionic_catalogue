package ru.aakumykov.me.sociocat.interfaces;

import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.models.User;

public interface iAuthSingleton {

    void resetPasswordEmail(String email, ResetPasswordCallbacks callbacks);

    interface ResetPasswordCallbacks {
        void onEmailSendSuccess();
        void onEmailSendFail(String errorMsg);
    }
}
