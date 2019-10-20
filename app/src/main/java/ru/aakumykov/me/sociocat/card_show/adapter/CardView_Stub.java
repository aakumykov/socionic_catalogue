package ru.aakumykov.me.sociocat.card_show.adapter;

import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.card_show.iCardShow;
import ru.aakumykov.me.sociocat.models.Card;

public class CardView_Stub implements iCardShow.iCardView {

    @Override
    public void displayCard(@Nullable Card card, @Nullable iDisplayCardCallbacks callbacks) throws Exception {

    }

    @Override
    public void showCardThrobber() {

    }

    @Override
    public void hideCardThrobber() {

    }

    @Override
    public void showCardError(int errorMsgId, String errorMsg) {

    }

    @Override
    public void hideCardError() {

    }

    @Override
    public void showCardDeleteDialog(Card card) {

    }


}
