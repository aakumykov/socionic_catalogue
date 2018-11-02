package ru.aakumykov.me.mvp.login;

import ru.aakumykov.me.mvp.iBaseView;
import ru.aakumykov.me.mvp.interfaces.iAuthService;
import ru.aakumykov.me.mvp.interfaces.iCardsService;

public interface iLogin {

    interface View extends iBaseView {
        void disableForm();
        void enableForm();
        void finishLogin(boolean byCancel);
    }

    interface Presenter {
        void doLogin(String email, String password) throws Exception;
        void cancelLogin();

        // TODO: вынести в общий интерфейс
        void linkView(iLogin.View view);
        void unlinkView();

        void linkModel(iCardsService model);
        void unlinkModel();

        void linkAuth(iAuthService authService);
        void unlinkAuth();
    }
}
