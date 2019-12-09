package ru.aakumykov.me.sociocat.card_show.list_items;

public class LoadMore_Item extends Base_Item {

    int textId = -1;

    public LoadMore_Item(int buttonTextId) {
        super(iList_Item.LOAD_MORE);
        this.textId = buttonTextId;
    }

    public int getTextId() {
        return this.textId;
    }
}
