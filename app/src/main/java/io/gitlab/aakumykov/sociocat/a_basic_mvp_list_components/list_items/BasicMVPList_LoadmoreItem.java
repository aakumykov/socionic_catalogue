package io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.list_items;


import androidx.annotation.NonNull;

import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.interfaces.iListBottomItem;

public class BasicMVPList_LoadmoreItem extends BasicMVPList_ListItem implements iListBottomItem {

    private int titleId;

    public BasicMVPList_LoadmoreItem() { }

    public BasicMVPList_LoadmoreItem(int titleId) {
        this.titleId = titleId;
    }

    @NonNull
    @Override
    public String toString() {
        return BasicMVPList_LoadmoreItem.class.getSimpleName() + " { }";
    }

    public int getTitleId() {
        return titleId;
    }

    public boolean hasTitleId() {
        return this.titleId > 0;
    }
}
