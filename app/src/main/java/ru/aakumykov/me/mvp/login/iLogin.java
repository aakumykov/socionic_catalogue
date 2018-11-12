package ru.aakumykov.me.mvp.login;

import ru.aakumykov.me.mvp.iBaseView;
import ru.aakumykov.me.mvp.interfaces.iAuthSingleton;
import ru.aakumykov.me.mvp.interfaces.iCardsSingleton;

public interface iLogin {

    interface View extends iBaseView {
        void disableForm();
        void enableForm();
        void finishLogin(boolean byCancel);
    }

    interface Presenter {
        void doLogin(String email, String password);
        void cancelLogin();

        // TODO: вынести в общий интерфейс
        void linkView(iLogin.View view);
        void unlinkView();
    }
}
