package io.gitlab.aakumykov.sociocat.card_show.list_items;

import io.gitlab.aakumykov.sociocat.models.Card;
import io.gitlab.aakumykov.sociocat.models.Comment;

public interface iList_Item {

    int CARD = 10;
    int COMMENT = 20;
    int LOAD_MORE = 30;
    int CARD_THROBBER = 40;
    int COMMENT_THROBBER = 50;

    int getItemType();

    Object getPayload();

    static boolean isCardItem(iList_Item listItem) {
        return listItem.getPayload() instanceof Card;
    }

    static boolean isCommentItem(iList_Item listItem) {
        return listItem.getPayload() instanceof Comment;
    }
}
