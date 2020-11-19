package ru.aakumykov.me.sociocat.b_basic_mvp_components2.list_items;


import androidx.annotation.NonNull;

import ru.aakumykov.me.sociocat.b_basic_mvp_components2.interfaces.iListBottomItem;

public class BasicMVP_LoadmoreItem extends BasicMVP_ListItem implements iListBottomItem {

    private int titleId;

    public BasicMVP_LoadmoreItem() { }

    public BasicMVP_LoadmoreItem(int titleId) {
        this.titleId = titleId;
    }

    @NonNull
    @Override
    public String toString() {
        return BasicMVP_LoadmoreItem.class.getSimpleName() + " { }";
    }

    public int getTitleId() {
        return titleId;
    }

    public boolean hasTitleId() {
        return this.titleId > 0;
    }
}