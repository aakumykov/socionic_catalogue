package ru.aakumykov.me.sociocat.tags_lsit3.stubs;

import android.widget.Filter;

import java.util.List;

import ru.aakumykov.me.sociocat.models.Tag;
import ru.aakumykov.me.sociocat.tags_lsit3.iTagsList3;

public class TagsList3_DataAdapter_Stub implements iTagsList3.iDataAdapter {

    @Override
    public boolean isVirgin() {
        return false;
    }

    @Override
    public void setList(List<Tag> inputList) {

    }

    @Override
    public void setList(List<Tag> inputList, CharSequence filterQuery) {

    }

    @Override
    public void appendList(List<Tag> inputList) {

    }

    @Override
    public Tag getTag(int position) {
        return null;
    }

    @Override
    public void removeTag(Tag tag) {

    }

    @Override
    public void sortByName(iTagsList3.SortingListener sortingListener) {

    }

    @Override
    public void sortByCount(iTagsList3.SortingListener sortingListener) {

    }

    @Override
    public int getListSize() {
        return 0;
    }

    @Override
    public iTagsList3.SortOrder getSortingMode() {
        return null;
    }

    @Override
    public Filter getFilter() {
        return null;
    }
}
