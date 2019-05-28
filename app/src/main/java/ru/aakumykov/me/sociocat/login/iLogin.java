package ru.aakumykov.me.sociocat.login;

import android.content.Intent;
import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.interfaces.iBaseView;

public interface iLogin {

    interface View extends iBaseView {
        void disableForm();
        void enableForm();
        void finishLogin(boolean byCancel);
        void notifyToConfirmEmail(String userId);
        void proceedLoginRequest(Intent intent);
    }

    interface Presenter {
        void doLogin(String email, String password);
        void cancelLogin();
        void processInputIntent(@Nullable Intent intent);

        // TODO: вынести в общий интерфейс
        void linkView(iLogin.View view);
        void unlinkView();
    }
}
