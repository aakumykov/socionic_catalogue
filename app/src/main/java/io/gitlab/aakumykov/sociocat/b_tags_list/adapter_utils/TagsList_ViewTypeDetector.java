package io.gitlab.aakumykov.sociocat.b_tags_list.adapter_utils;

import androidx.annotation.NonNull;

import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.adapter_utils.BasicMVPList_ViewTypeDetector;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.data_types.BasicMVPList_ItemTypes;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.list_items.BasicMVPList_ListItem;
import io.gitlab.aakumykov.sociocat.b_tags_list.list_items.Tag_ListItem;

public class TagsList_ViewTypeDetector extends BasicMVPList_ViewTypeDetector {

    @Override
    public int getItemType(@NonNull BasicMVPList_ListItem listItem) {

        if (listItem instanceof Tag_ListItem)
            return BasicMVPList_ItemTypes.DATA_ITEM;

        return super.getItemType(listItem);
    }
}
