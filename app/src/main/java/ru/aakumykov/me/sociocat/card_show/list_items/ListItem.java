package ru.aakumykov.me.sociocat.card_show.list_items;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.models.Comment;

@IgnoreExtraProperties
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


    @Override @Exclude
    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }

    @Override @Exclude
    public ItemType getItemType() {
        if (this instanceof Card) return ItemType.CARD_ITEM;
        if (this instanceof Comment) return ItemType.COMMENT_ITEM;
        return itemType;
    }

    @Override @Exclude
    public boolean is(ItemType testItemType) {
        return testItemType.equals(itemType);
    }

    @Override @Exclude
    public boolean isCardItem() {
        return (this instanceof Card);
    }

    @Override @Exclude
    public boolean isCommentItem() {
        return (this instanceof Comment);
    }

    @Override @Exclude
    public boolean isCommentsThrobberItem() {
        return ItemType.CARD_ITEM.equals(itemType);
    }

    @Override @Exclude
    public boolean isLoadMoreItem() {
        return ItemType.LOAD_MORE_ITEM.equals(itemType);
    }
}
