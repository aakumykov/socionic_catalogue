package ru.aakumykov.me.sociocat.a_basic_mvp_components.list_Items;

import androidx.annotation.NonNull;

import ru.aakumykov.me.sociocat.a_basic_mvp_components.interfaces.iListBottomItem;


public class Basic_LoadmoreItem extends Basic_ListItem implements iListBottomItem {

    private int titleId;

    public Basic_LoadmoreItem() { }

    public Basic_LoadmoreItem(int titleId) {
        this.titleId = titleId;
    }

    @NonNull @Override
    public String toString() {
        return Basic_LoadmoreItem.class.getSimpleName() + " { }";
    }

    public int getTitleId() {
        return titleId;
    }

    public boolean hasTitleId() {
        return this.titleId > 0;
    }
}
