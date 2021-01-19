package io.gitlab.aakumykov.sociocat.b_comments_list.adapter_utils;

import androidx.annotation.NonNull;

import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.adapter_utils.BasicMVPList_ViewTypeDetector;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.data_types.BasicMVPList_ItemTypes;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.list_items.BasicMVPList_ListItem;
import io.gitlab.aakumykov.sociocat.b_comments_list.list_items.Comment_ListItem;

public class CommentsList_ViewTypeDetector extends BasicMVPList_ViewTypeDetector {

    @Override
    public int getItemType(@NonNull BasicMVPList_ListItem listItem) {

        if (listItem instanceof Comment_ListItem)
            return BasicMVPList_ItemTypes.DATA_ITEM;

        return super.getItemType(listItem);
    }
}
