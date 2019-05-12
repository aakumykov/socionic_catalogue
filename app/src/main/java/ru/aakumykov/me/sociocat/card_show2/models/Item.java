package ru.aakumykov.me.sociocat.card_show2.models;

public abstract class Item {

    public static final int CARD_VIEW_TYPE = 1;
    public static final int COMMENT_VIEW_TYPE = 2;
    public static final int LOAD_MORE_VIEW_TYPE = 3;
    public static final int COMMENTS_THROBBER_VIEW_TYPE = 4;

    public enum ItemType {
        CARD_ITEM,
        COMMENT_ITEM,
        LOAD_MORE_ITEM,
        COMMENTS_THROBBER_ITEM
    }

    public abstract int getKey();

    public abstract ItemType getItemType();
}
