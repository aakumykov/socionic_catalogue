package ru.aakumykov.me.mvp.template_active_view;

import ru.aakumykov.me.mvp.cards_list_av.iCardsListAV;
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

        void linkModel(iCardsService model);
        void unlinkModel();

        void linkAuth(iAuthService authService);
        void unlinkAuth();
    }
}
