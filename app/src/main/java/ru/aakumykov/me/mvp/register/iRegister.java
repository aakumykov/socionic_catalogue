package ru.aakumykov.me.mvp.register;

import ru.aakumykov.me.mvp.iBaseView;
import ru.aakumykov.me.mvp.interfaces.iAuthService;
import ru.aakumykov.me.mvp.interfaces.iCardsService;

public interface iRegister {

    interface View extends iBaseView {
        void disableForm();
        void enableForm();
    }

    interface Presenter {
        void regUserWithEmail(String name, String email, String password);

        // TODO: вынести в общий интерфейс
        void linkView(iRegister.View view);
        void unlinkView();

        void linkModel(iCardsService model);
        void unlinkModel();

        void linkAuth(iAuthService authService);
        void unlinkAuth();
    }
}
