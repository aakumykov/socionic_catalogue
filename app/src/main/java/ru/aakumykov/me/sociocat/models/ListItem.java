package ru.aakumykov.me.sociocat.models;

import com.google.firebase.database.Exclude;

public abstract class ListItem {

    public static final int CARD_VIEW_TYPE = 1;
    public static final int COMMENT_VIEW_TYPE = 2;
    public static final int LOAD_MORE_VIEW_TYPE = 3;
    public static final int COMMENTS_THROBBER_VIEW_TYPE = 4;
    public static final int CARD_THROBBER_VIEW_TYPE = 5;
    public static final int CARD_ERROR_VIEW_TYPE = 6;

    public enum ItemType {
        CARD_ITEM,
        COMMENT_ITEM,
        LOAD_MORE_ITEM,
        COMMENTS_THROBBER_ITEM,
        CARD_THROBBER_ITEM,
        CARD_ERROR_ITEM
    }

    @Exclude private ItemType itemType;
    @Exclude private Integer viewType;


    // Внешние методы
    void setItemType(ItemType itemType) {
        this.itemType = itemType;

        switch (itemType) {
            case CARD_ITEM:
                setViewType(CARD_VIEW_TYPE);
                break;

            case CARD_THROBBER_ITEM:
                setViewType(CARD_THROBBER_VIEW_TYPE);
                break;

            case CARD_ERROR_ITEM:
                setViewType(CARD_VIEW_TYPE);
                break;

            case COMMENT_ITEM:
                setViewType(COMMENT_VIEW_TYPE);
                break;

            case COMMENTS_THROBBER_ITEM:
                setViewType(COMMENTS_THROBBER_VIEW_TYPE);

            case LOAD_MORE_ITEM:
                setViewType(LOAD_MORE_VIEW_TYPE);

            default:
                setViewType(-1);
        }
    }

    public int getViewType() {
        return itemType.ordinal();
    }


    public boolean is(ItemType testItemType) {
        return itemType.equals(testItemType);
    }

    public boolean isCommentItem() {
        return itemType.equals(ItemType.COMMENT_ITEM);
    }

    public boolean isCommentsThrobberItem() {
        return itemType.equals(ItemType.CARD_ITEM);
    }

    public boolean isLoadMoreItem() {
        return itemType.equals(ItemType.LOAD_MORE_ITEM);
    }


    // Внутренние методы
    private void setViewType(Integer viewType) {
        this.viewType = viewType;
    }

}
