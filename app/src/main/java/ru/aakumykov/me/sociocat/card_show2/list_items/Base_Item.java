package ru.aakumykov.me.sociocat.card_show2.list_items;

import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.models.Comment;

public class Base_Item implements iList_Item {

    private Object payload;
    private int itemType;


    Base_Item(int itemType) {
        this(itemType, null);
    }

    Base_Item(int itemType, @Nullable Object payload) {
        this.itemType = itemType;
        this.payload = payload;
    }

    @Override
    public int getItemType() {
        return this.itemType;
    }

    @Override
    public Object getPayload() {
        return this.payload;
    }

}
