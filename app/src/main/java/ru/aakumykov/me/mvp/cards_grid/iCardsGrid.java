package ru.aakumykov.me.mvp.cards_grid;

import java.util.List;

import ru.aakumykov.me.mvp.cards_list.iCardsList;
import ru.aakumykov.me.mvp.iBaseView;
import ru.aakumykov.me.mvp.models.Card;

public interface iCardsGrid {

    interface ListCallbacks {
        void onListLoadSuccess(List<Card> list);
        void onListLoadFail(String errorMsg);
    }

    interface View extends iBaseView {
        void displayList(List<Card> list);
    }

    interface Presenter {
        void loadCards();

        void linkView(iCardsGrid.View view);
        void unlinkView();
    }
}
