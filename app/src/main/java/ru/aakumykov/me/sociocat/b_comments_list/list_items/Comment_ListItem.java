package ru.aakumykov.me.sociocat.b_comments_list.list_items;

import androidx.annotation.NonNull;

import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.list_items.BasicMVPList_DataItem;
import ru.aakumykov.me.sociocat.models.Tag;

public class Comment_ListItem extends BasicMVPList_DataItem {

    public Comment_ListItem(Tag tag) {
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
