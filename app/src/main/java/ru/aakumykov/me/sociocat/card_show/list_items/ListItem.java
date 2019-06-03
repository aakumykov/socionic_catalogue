package ru.aakumykov.me.sociocat.card_show.list_items;

import com.google.firebase.database.Exclude;

public abstract class ListItem implements iListItem {

    public static final int CARD_VIEW_TYPE = 10;
    public static final int COMMENT_VIEW_TYPE = 20;
    public static final int LOAD_MORE_VIEW_TYPE = 30;
    public static final int THROBBER_VIEW_TYPE = 40;

    public enum ItemType {
        CARD_ITEM,
        COMMENT_ITEM,
        LOAD_MORE_ITEM,
        THROBBER_ITEM
    }

    @Exclude private ItemType itemType;


    @Override
    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }

    @Override @Exclude
    public ItemType getItemType() {
        return itemType;
    }

    @Override @Exclude
    public boolean is(ItemType testItemType) {
        return itemType.equals(testItemType);
    }

    @Override @Exclude
    public boolean isCardItem() {
        return itemType.equals(ItemType.CARD_ITEM);
    }

    @Override @Exclude
    public boolean isCommentItem() {
        return itemType.equals(ItemType.COMMENT_ITEM);
    }

    @Override @Exclude
    public boolean isCommentsThrobberItem() {
        return itemType.equals(ItemType.CARD_ITEM);
    }

    @Override @Exclude
    public boolean isLoadMoreItem() {
        return itemType.equals(ItemType.LOAD_MORE_ITEM);
    }
}
