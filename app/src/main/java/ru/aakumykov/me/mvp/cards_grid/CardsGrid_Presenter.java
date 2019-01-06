package ru.aakumykov.me.mvp.cards_grid;

import java.util.List;

import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.interfaces.iCardsSingleton;
import ru.aakumykov.me.mvp.models.Card;
import ru.aakumykov.me.mvp.services.CardsSingleton;

public class CardsGrid_Presenter implements
        iCardsGrid.Presenter
{
    private iCardsGrid.View view;
    private iCardsSingleton cardsService = CardsSingleton.getInstance();


    // Системные методы
    @Override
    public void linkView(iCardsGrid.View view) {
        this.view = view;
    }

    @Override
    public void unlinkView() {
        this.view = null;
    }


    // Интерфейсные методы
    @Override
    public void loadCards() {
        cardsService.loadList(new iCardsSingleton.ListCallbacks() {
            @Override
            public void onListLoadSuccess(List<Card> list) {
                view.displayList(list);
            }

            @Override
            public void onListLoadFail(String errorMessage) {
                view.showErrorMsg(errorMessage);
            }
        });
    }
}
