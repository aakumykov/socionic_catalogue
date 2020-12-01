package ru.aakumykov.me.sociocat.a_basic_mvp_list_components.adapter_utils;


import androidx.annotation.NonNull;

import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.data_types.BasicMVPList_ItemTypes;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.list_items.BasicMVPList_ListItem;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.list_items.BasicMVPList_LoadmoreItem;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.list_items.BasicMVPList_ThrobberItem;

public abstract class BasicMVPList_ViewTypeDetector {

    public int getItemType(@NonNull BasicMVPList_ListItem listItem) {

        if (listItem instanceof BasicMVPList_LoadmoreItem) {
            return BasicMVPList_ItemTypes.LOADMORE_ITEM;
        }
        else if (listItem instanceof BasicMVPList_ThrobberItem) {
            return BasicMVPList_ItemTypes.THROBBER_ITEM;
        }
        else {
            throw new RuntimeException("Объект iListItem неизвестного типа: "+listItem);
        }
    }
}