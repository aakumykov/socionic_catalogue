package ru.aakumykov.me.sociocat.models;

import ru.aakumykov.me.sociocat.models.Item;

public class CommentsThrobber extends Item {

    @Override
    public ItemType getItemType() {
        return ItemType.COMMENTS_THROBBER_ITEM;
    }

    @Override
    public String getKey() {
        return "";
    }
}
