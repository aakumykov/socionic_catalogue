package ru.aakumykov.me.sociocat.card_show2.list_items;

import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.models.Comment;

public class List_Item implements iList_Item {

    private Object payload;
    private int itemType;


    public List_Item(Object payload) {
        this.payload = payload;

        if (payload instanceof Card) {
            itemType = iList_Item.CARD;
        }
        else if (payload instanceof Comment) {
            itemType = iList_Item.COMMENT;
        }
        else {
            itemType = iList_Item.UNKNOWN;
        }
    }

    @Override
    public int getItemType() {
        return this.itemType;
    }
}
