package ru.aakumykov.me.sociocat.tags_list.adapter_utils;

import androidx.annotation.NonNull;

import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.adapter_utils.BasicMVP_ViewTypeDetector;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.data_types.BasicMVP_ItemTypes;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.list_items.BasicMVP_ListItem;
import ru.aakumykov.me.sociocat.tags_list.list_parts.Tag_ListItem;

public class TagsList_ViewTypeDetector extends BasicMVP_ViewTypeDetector {

    @Override
    public int getItemType(@NonNull BasicMVP_ListItem listItem) {

        if (listItem instanceof Tag_ListItem)
            return BasicMVP_ItemTypes.DATA_ITEM;

        return super.getItemType(listItem);
    }
}
