package ru.aakumykov.me.sociocat.cards_list2.list_items;

import androidx.annotation.NonNull;

import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.list_items.BasicMVPList_DataItem;
import ru.aakumykov.me.sociocat.models.Card;

public class Card_ListItem extends BasicMVPList_DataItem {

    public Card_ListItem(Card card) {
        setPayload(card);
    }

    @Override
    public String getTitle() {
        return ((Card) getPayload()).getTitle();
    }

    @NonNull @Override
    public String toString() {
        return "Card_ListItem { " + getTitle() + " }";
    }
}
