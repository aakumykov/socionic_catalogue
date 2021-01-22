package io.gitlab.aakumykov.sociocat.b_tags_list.list_items;

import androidx.annotation.NonNull;

import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.list_items.BasicMVPList_DataItem;
import io.gitlab.aakumykov.sociocat.models.Tag;

public class Tag_ListItem extends BasicMVPList_DataItem {

    public Tag_ListItem(Tag tag) {
        setPayload(tag);
    }

    @Override
    public String getTitle() {
        return getTag().getName();
    }

    @NonNull
    @Override
    public String toString() {
        return "Tag { " + getTag().getName() + " }";
    }

    private Tag getTag() {
        return (Tag) getPayload();
    }
}
