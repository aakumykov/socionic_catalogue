package ru.aakumykov.me.sociocat.a_basic_mvp_components.list_Items;

import androidx.annotation.NonNull;

import ru.aakumykov.me.sociocat.a_basic_mvp_components.interfaces.iListBottomItem;


public class BasicMVP_ThrobberItem extends BasicMVP_ListItem implements iListBottomItem {

    @NonNull
    @Override
    public String toString() {
        return BasicMVP_ThrobberItem.class.getSimpleName() + " { }";
    }

}
