package io.gitlab.aakumykov.sociocat.b_tags_list.list_utils;

import androidx.annotation.NonNull;

import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.list_items.BasicMVPList_DataItem;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.list_utils.BasicMVPList_ItemsTextFilter;
import io.gitlab.aakumykov.sociocat.models.Tag;

public class TagsList_ItemsTextFilter extends BasicMVPList_ItemsTextFilter {

    @Override
    protected boolean testDataItem(@NonNull BasicMVPList_DataItem dataItem, @NonNull String filterPattern) {

        Tag tag = (Tag) dataItem.getPayload();

        String title = tag.getName().toLowerCase();
        String patternReal = filterPattern.toLowerCase();

        return title.contains(patternReal);
    }
}
