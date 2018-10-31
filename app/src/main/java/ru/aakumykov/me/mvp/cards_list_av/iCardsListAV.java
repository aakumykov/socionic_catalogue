package ru.aakumykov.me.mvp.cards_list_av;

import java.util.List;

import ru.aakumykov.me.mvp.iBaseView;
import ru.aakumykov.me.mvp.interfaces.iAuthService;
import ru.aakumykov.me.mvp.interfaces.iCardsService;
import ru.aakumykov.me.mvp.interfaces.iDialogCallbacks;
import ru.aakumykov.me.mvp.models.Card;

public interface iCardsListAV {

    interface View extends iBaseView {
        void displayList(List<Card> list);
        void deleteCardRequest(iDialogCallbacks.Delete callbacks);
    }

    interface Presenter {

        void loadList();
        void deleteCard(final Card card);

        // TODO: вынести в общий интерфейс
        void linkView(iCardsListAV.View view);
        void unlinkView();

        void linkModel(iCardsService model);
        void unlinkModel();

        void linkAuth(iAuthService authService);
        void unlinkAuth();
    }
}
