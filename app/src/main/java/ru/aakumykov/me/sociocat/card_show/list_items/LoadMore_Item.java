package ru.aakumykov.me.sociocat.card_show.list_items;


import ru.aakumykov.me.sociocat.models.Comment;

public class LoadMore_Item extends ListItem {

    private Comment lastVisibleComment;

    public LoadMore_Item(Comment lastVisibleComment) {
        setItemType(ItemType.LOAD_MORE_ITEM);
        this.lastVisibleComment = lastVisibleComment;
    }

    public Comment getLastVisibleComment() {
        return lastVisibleComment;
    }
}
