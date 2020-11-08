package ru.aakumykov.me.sociocat.tags_list;

import androidx.annotation.NonNull;

import ru.aakumykov.me.sociocat.a_basic_mvp_components.list_Items.BasicMVP_DataItem;
import ru.aakumykov.me.sociocat.models.Tag;

public class Tag_ListItem extends BasicMVP_DataItem {

    public Tag_ListItem(Object object) {
        setPayload(object);
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
