package ru.aakumykov.me.sociocat.tags_lsit3.stubs;

import java.util.List;

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
    public void setList(List<Item> inputList) {

    }

    @Override
    public void appendList(List<Item> tagsList) {

    }

    @Override
    public Item getTag(int position) {
        return null;
    }

    @Override
    public void removeItem(Item item) {

    }

    @Override
    public int getListSize() {
        return 0;
    }
}
