package ru.aakumykov.me.sociocat.card_show2.models;

public class CommentsThrobber extends Item {

    @Override
    public ItemType getItemType() {
        return ItemType.COMMENTS_THROBBER_ITEM;
    }

    @Override
    public int getKey() {
        return -1;
    }
}
