package ru.aakumykov.me.sociocat.a_basic_mvp_components.adapter_utils;

import androidx.annotation.NonNull;

import ru.aakumykov.me.sociocat.a_basic_mvp_components.data_types.BasicMVP_ItemTypes;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.list_Items.BasicMVP_ListItem;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.list_Items.BasicMVP_LoadmoreItem;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.list_Items.BasicMVP_ThrobberItem;


public abstract class BasicMVP_ViewTypeDetector {

    public int getItemType(@NonNull BasicMVP_ListItem listItem) {

        if (listItem instanceof BasicMVP_LoadmoreItem) {
            return BasicMVP_ItemTypes.LOADMORE_ITEM;
        }
        else if (listItem instanceof BasicMVP_ThrobberItem) {
            return BasicMVP_ItemTypes.THROBBER_ITEM;
        }
        else {
            throw new RuntimeException("Объект iListItem неизвестного типа: "+listItem);
        }
    }

}