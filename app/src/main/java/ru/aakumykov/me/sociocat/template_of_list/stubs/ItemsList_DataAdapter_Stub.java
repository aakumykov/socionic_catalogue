package ru.aakumykov.me.sociocat.template_of_list.stubs;

import java.util.List;

import ru.aakumykov.me.sociocat.template_of_list.iItemsList;
import ru.aakumykov.me.sociocat.template_of_list.model.Item;

public class ItemsList_DataAdapter_Stub implements iItemsList.iDataAdapter {

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
    public Item getItem(int position) {
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