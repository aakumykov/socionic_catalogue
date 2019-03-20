package ru.aakumykov.me.sociocat.cards_grid;

import java.util.List;

import ru.aakumykov.me.sociocat.interfaces.iBaseView;
import ru.aakumykov.me.sociocat.models.Card;

public interface iCardsGrid {

    interface ListCallbacks {
        void onListLoadSuccess(List<Card> list);
        void onListLoadFail(String errorMsg);
    }

    interface View extends iBaseView {
        void displayList(List<Card> list);
        void removeGridItem(Card card);
    }

    interface Presenter {
        void linkView(iCardsGrid.View view);
        void unlinkView();

        void loadCards();
        void loadNewCards(long newerThanTime);

        void deleteCard(Card card);
    }
}