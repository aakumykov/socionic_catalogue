package ru.aakumykov.me.mvp.cards_list_av;

import java.util.List;

import ru.aakumykov.me.mvp.interfaces.iAuthService;
import ru.aakumykov.me.mvp.interfaces.iCardsService;
import ru.aakumykov.me.mvp.models.Card;

public interface iCardsListAV {

    interface View {
        void displayList(List<Card> cardsList);
    }

    interface Presenter {

        void deleteCard(Card card);

        // TODO: вынести в общий интерфейс
        void linkView(iCardsListAV.View view);
        void unlinkView();

        void linkModel(iCardsService model);
        void unlinkModel();

        void linkAuth(iAuthService authService);
        void unlinkAuth();
    }
}
