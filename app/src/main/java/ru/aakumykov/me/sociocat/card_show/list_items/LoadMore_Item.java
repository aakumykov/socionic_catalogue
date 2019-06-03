package ru.aakumykov.me.sociocat.card_show.list_items;


import ru.aakumykov.me.sociocat.models.Comment;

public class LoadMore_Item extends ListItem {

    private Comment startAtComment;

    public LoadMore_Item(Comment startAtComment) {
        setItemType(ItemType.LOAD_MORE_ITEM);
        this.startAtComment = startAtComment;
    }

    public Comment getStartAtComment() {
        return startAtComment;
    }
}
