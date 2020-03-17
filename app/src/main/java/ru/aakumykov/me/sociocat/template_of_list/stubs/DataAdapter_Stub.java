package ru.aakumykov.me.sociocat.template_of_list.stubs;

import android.widget.Filter;

import java.util.List;

import ru.aakumykov.me.sociocat.template_of_list.iItemsList;
import ru.aakumykov.me.sociocat.template_of_list.model.Item;

public class DataAdapter_Stub
        extends SelectableAdapter_Stub
        implements iItemsList.iDataAdapter
{
    @Override
    public boolean isVirgin() {
        return false;
    }

    @Override
    public void setList(List<Item> inputList) {

    }

    @Override
    public void setList(List<Item> inputList, CharSequence filterQuery) {

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

    @Override
    public void sortByName(iItemsList.SortingListener sortingListener) {

    }

    @Override
    public void sortByCount(iItemsList.SortingListener sortingListener) {

    }

    @Override
    public iItemsList.SortingMode getSortingMode() {
        return null;
    }

    @Override
    public int getPositionOf(Item item) {
        return 0;
    }

    @Override
    public Filter getFilter() {
        return null;
    }
}
