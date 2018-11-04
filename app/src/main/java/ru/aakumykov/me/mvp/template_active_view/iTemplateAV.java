package ru.aakumykov.me.mvp.template_active_view;

import ru.aakumykov.me.mvp.iBaseView;
import ru.aakumykov.me.mvp.interfaces.iAuthService;
import ru.aakumykov.me.mvp.interfaces.iCardsService;

public interface iTemplateAV {

    interface View extends iBaseView {
        void display();
    }

    interface Presenter {
        void load();

        // TODO: вынести в общий интерфейс
        void linkView(iTemplateAV.View view);
        void unlinkView();

        void linkCardsService(iCardsService model);
        void unlinkCardsService();

        void linkAuth(iAuthService authService);
        void unlinkAuthService();
    }
}
