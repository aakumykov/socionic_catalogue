package ru.aakumykov.me.sociocat.template_active_view;

import ru.aakumykov.me.sociocat.interfaces.iBaseView;
import ru.aakumykov.me.sociocat.interfaces.iAuthSingleton;
import ru.aakumykov.me.sociocat.interfaces.iCardsSingleton;

public interface iTemplateAV {

    interface View extends iBaseView {
        void display();
    }

    interface Presenter {
        void load();

        // TODO: вынести в общий интерфейс
        void linkView(iTemplateAV.View view);
        void unlinkView();

        void linkCardsService(iCardsSingleton model);
        void unlinkCardsService();

        void linkAuth(iAuthSingleton authSingleton);
        void unlinkAuthService();
    }
}
