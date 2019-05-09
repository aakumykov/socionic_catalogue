package ru.aakumykov.me.sociocat.models;

import ru.aakumykov.me.sociocat.models.Item;

public class LoadMore extends Item {

    @Override
    public ItemType getItemType() {
        return ItemType.LOAD_MORE_ITEM;
    }

    @Override
    public String getKey() {
        return "";
    }
}
