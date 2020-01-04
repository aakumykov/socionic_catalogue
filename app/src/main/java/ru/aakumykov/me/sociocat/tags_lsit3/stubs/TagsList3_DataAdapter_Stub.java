package ru.aakumykov.me.sociocat.tags_lsit3.stubs;

import java.util.List;

import ru.aakumykov.me.sociocat.models.Tag;
import ru.aakumykov.me.sociocat.tags_lsit3.iTagsList3;
import ru.aakumykov.me.sociocat.tags_lsit3.model.Item;

public class TagsList3_DataAdapter_Stub implements iTagsList3.iDataAdapter {

    @Override
    public boolean isVirgin() {
        return false;
    }

    @Override
    public void deflorate() {

    }

    @Override
    public void setList(List<Tag> inputList) {

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
    public int getListSize() {
        return 0;
    }

    @Override
    public void sortListByName() {

    }

    @Override
    public void sortListByCardsCount() {

    }
}
