package ru.aakumykov.me.sociocat.a_basic_mvp_list_components.list_items;


import androidx.annotation.NonNull;

import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.interfaces.iListBottomItem;

public class BasicMVP_ThrobberItem extends BasicMVP_ListItem implements iListBottomItem {

    @NonNull
    @Override
    public String toString() {
        return BasicMVP_ThrobberItem.class.getSimpleName() + " { }";
    }

}
