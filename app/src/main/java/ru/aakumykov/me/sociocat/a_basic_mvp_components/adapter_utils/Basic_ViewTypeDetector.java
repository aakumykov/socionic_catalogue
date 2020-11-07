package ru.aakumykov.me.sociocat.a_basic_mvp_components.adapter_utils;

import androidx.annotation.NonNull;

import ru.aakumykov.me.sociocat.a_basic_mvp_components.data_types.ItemTypes;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.list_Items.Basic_ListItem;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.list_Items.Basic_LoadmoreItem;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.list_Items.Basic_ThrobberItem;


public abstract class Basic_ViewTypeDetector {

    public int getItemType(@NonNull Basic_ListItem listItem) {

        if (listItem instanceof Basic_LoadmoreItem) {
            return ItemTypes.LOADMORE_ITEM;
        }
        else if (listItem instanceof Basic_ThrobberItem) {
            return ItemTypes.THROBBER_ITEM;
        }
        else {
            throw new RuntimeException("Объект iListItem неизвестного типа: "+listItem);
        }
    }

}