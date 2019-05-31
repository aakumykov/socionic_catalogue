package ru.aakumykov.me.sociocat.card_show.adapter;

import android.content.Context;

import ru.aakumykov.me.sociocat.card_show.list_items.ListItem;
import ru.aakumykov.me.sociocat.models.Card;

public class CardView_Stub implements iCardView {

    @Override public Context getPageContext() {
        return null;
    }

    @Override public void showCardThrobber() {

    }

    @Override public void hideCardThrobber() {

    }

    @Override public void showCardError(int errorMsgId, String errorMsg) {

    }

    @Override public void hideCardError() {

    }

    @Override public void showCommentForm(ListItem repliedItem) {

    }

    @Override public void displayCard(Card card) {

    }
}
