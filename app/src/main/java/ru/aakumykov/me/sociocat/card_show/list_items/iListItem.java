package ru.aakumykov.me.sociocat.card_show.list_items;

import com.google.firebase.firestore.Exclude;

public interface iListItem {

    void setItemType(ListItem.ItemType itemType);

    @Exclude ListItem.ItemType getItemType();

    @Exclude boolean is(ListItem.ItemType testItemType);

    @Exclude boolean isCardItem();

    @Exclude boolean isCommentItem();

    @Exclude boolean isCommentsThrobberItem();

    @Exclude boolean isLoadMoreItem();
}
