package ru.aakumykov.me.sociocat.card_show.list_items;


import ru.aakumykov.me.sociocat.models.Comment;

public class LoadMore_Item extends ListItem {

    private String lastCommentKey;

    public LoadMore_Item(Comment lastComment) {
        setItemType(ItemType.LOAD_MORE_ITEM);
        this.lastCommentKey = lastComment.getKey();
    }

    public String getLastCommentKey() {
        return lastCommentKey;
    }
}
