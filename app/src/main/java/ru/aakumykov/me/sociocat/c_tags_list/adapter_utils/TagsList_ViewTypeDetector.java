package ru.aakumykov.me.sociocat.c_tags_list.adapter_utils;

import androidx.annotation.NonNull;

import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.adapter_utils.BasicMVPList_ViewTypeDetector;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.data_types.BasicMVPList_ItemTypes;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.list_items.BasicMVPList_ListItem;
import ru.aakumykov.me.sociocat.c_tags_list.list_items.Tag_ListItem;

public class TagsList_ViewTypeDetector extends BasicMVPList_ViewTypeDetector {

    @Override
    public int getItemType(@NonNull BasicMVPList_ListItem listItem) {

        if (listItem instanceof Tag_ListItem)
            return BasicMVPList_ItemTypes.DATA_ITEM;

        return super.getItemType(listItem);
    }
}
