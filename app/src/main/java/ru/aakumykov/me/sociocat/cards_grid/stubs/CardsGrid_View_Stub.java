package ru.aakumykov.me.sociocat.cards_grid.stubs;

import ru.aakumykov.me.sociocat.BaseView_Stub;
import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.cards_grid.iCardsGrid;
import ru.aakumykov.me.sociocat.models.Card;

public class CardsGrid_View_Stub extends BaseView_Stub implements iCardsGrid.iPageView {

    @Override
    public <T> void setTitle(T title) {

    }

    @Override
    public void goShowCard(Card card, int position) {

    }

    @Override
    public void goCreateCard(Constants.CardType cardType) {

    }

    @Override
    public void goEditCard(Card card, int position) {

    }

    @Override
    public void goCardsGrid() {

    }

    @Override
    public String getCurrentFilterWord() {
        return null;
    }

    @Override
    public String getCurrentFilterTag() {
        return null;
    }

    @Override
    public void showTagFilter(String tagName) {

    }

    @Override
    public void showToolbarThrobber() {

    }

    @Override
    public void hideToolbarThrobber() {

    }

    @Override
    public void scroll2position(int position) {

    }

    @Override public void showSwipeThrobber() {

    }

    @Override public void hideSwipeThrobber() {

    }

}