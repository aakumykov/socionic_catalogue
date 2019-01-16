package ru.aakumykov.me.mvp.login;

import android.content.Intent;
import android.support.annotation.Nullable;

import ru.aakumykov.me.mvp.iBaseView;
import ru.aakumykov.me.mvp.interfaces.iAuthSingleton;
import ru.aakumykov.me.mvp.interfaces.iCardsSingleton;

public interface iLogin {

    interface View extends iBaseView {
        void disableForm();
        void enableForm();
        void finishLogin(boolean byCancel);
        void notifyCnfirmEmail();
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
