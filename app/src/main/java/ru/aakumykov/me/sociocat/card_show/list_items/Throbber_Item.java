package ru.aakumykov.me.sociocat.card_show.list_items;

public class Throbber_Item extends ListItem {

    private int messageId;

    public Throbber_Item(int messageId) {
        this.messageId = messageId;
        setItemType(ItemType.COMMENTS_THROBBER_ITEM);
    }

    public int getMessage() {
        return messageId;
    }
}
