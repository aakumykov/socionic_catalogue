package io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.list_items;


import androidx.annotation.NonNull;

import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.interfaces.iListBottomItem;

public class BasicMVPList_ThrobberItem extends BasicMVPList_ListItem implements iListBottomItem {

    @NonNull
    @Override
    public String toString() {
        return BasicMVPList_ThrobberItem.class.getSimpleName() + " { }";
    }

}
