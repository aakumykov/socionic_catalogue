package ru.aakumykov.me.sociocat.b_cards_list.list_items;

import androidx.annotation.NonNull;

import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.list_items.BasicMVPList_DataItem;
import ru.aakumykov.me.sociocat.models.Card;

public class Card_ListItem extends BasicMVPList_DataItem {

    public Card_ListItem(Card card) {
        setPayload(card);
    }

    @Override
    public String getTitle() {
        return getCard().getTitle();
    }

    @NonNull @Override
    public String toString() {
        return "Card_ListItem { " + getTitle() + ", comments: " + getCommentsCount() + " }";
    }

    private int getCommentsCount() {
        return getCard().getCommentsKeys().size();
    }

    private Card getCard() {
        return (Card) getPayload();
    }
}
