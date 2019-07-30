package ru.aakumykov.me.sociocat.card_show.adapter;

import ru.aakumykov.me.sociocat.card_show.iCardShow;
import ru.aakumykov.me.sociocat.models.Card;

public class CardView_Stub implements iCardShow.iCardView {
    @Override public void showCardThrobber() {

    }

    @Override public void hideCardThrobber() {

    }

    @Override public void showCardError(int errorMsgId, String errorMsg) {

    }

    @Override public void hideCardError() {

    }

    @Override
    public void showCardDeleteDialog(Card card) {

    }

    @Override public void displayCard(Card card) {

    }
}
