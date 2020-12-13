package ru.aakumykov.me.sociocat.c_tags_list.list_utils;

import android.util.Log;

import androidx.annotation.NonNull;

import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.list_items.BasicMVPList_DataItem;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.list_utils.BasicMVPList_ItemsTextFilter;
import ru.aakumykov.me.sociocat.models.Tag;

public class TagsList_ItemsTextFilter extends BasicMVPList_ItemsTextFilter {

    private static final String TAG = TagsList_ItemsTextFilter.class.getSimpleName();

    public TagsList_ItemsTextFilter() {
        Log.d(TAG, "new TagsList_ItemsTextFilter()");
    }

    @Override
    protected boolean testDataItem(@NonNull BasicMVPList_DataItem dataItem, @NonNull String filterPattern) {

        Tag tag = (Tag) dataItem.getPayload();

        String title = tag.getName().toLowerCase();
        String patternReal = filterPattern.toLowerCase();

        return title.contains(patternReal);
    }
}
