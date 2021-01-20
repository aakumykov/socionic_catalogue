package io.gitlab.aakumykov.sociocat.b_comments_list.list_utils;

import androidx.annotation.NonNull;

import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.list_items.BasicMVPList_DataItem;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.list_utils.BasicMVPList_ItemsTextFilter;
import io.gitlab.aakumykov.sociocat.models.Comment;

public class CommentsList_ItemsTextFilter extends BasicMVPList_ItemsTextFilter {

    @Override
    protected boolean testDataItem(@NonNull BasicMVPList_DataItem dataItem, @NonNull String filterPattern) {

        Comment tag = (Comment) dataItem.getPayload();

        String title = tag.getName().toLowerCase();
        String patternReal = filterPattern.toLowerCase();

        return title.contains(patternReal);
    }
}
