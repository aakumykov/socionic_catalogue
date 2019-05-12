package ru.aakumykov.me.sociocat.card_show2.models;

public class LoadMore extends Item {

    @Override
    public ItemType getItemType() {
        return ItemType.LOAD_MORE_ITEM;
    }

    @Override
    public int getKey() {
        return -1;
    }
}
