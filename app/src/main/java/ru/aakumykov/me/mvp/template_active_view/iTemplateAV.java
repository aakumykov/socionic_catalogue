package ru.aakumykov.me.mvp.template_active_view;

import ru.aakumykov.me.mvp.iBaseView;
import ru.aakumykov.me.mvp.interfaces.iAuthSingleton;
import ru.aakumykov.me.mvp.interfaces.iCardsSingleton;

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

        void linkAuth(iAuthSingleton authService);
        void unlinkAuthService();
    }
}
