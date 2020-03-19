package ru.aakumykov.me.sociocat.template_of_list.stubs;

import android.widget.Filter;

import java.util.List;

import ru.aakumykov.me.sociocat.template_of_list.iItemsList;
import ru.aakumykov.me.sociocat.template_of_list.model.DataItem;

public class ItemsList_DataAdapter_Stub
        extends SelectableAdapter_Stub
        implements iItemsList.iDataAdapter
{
    @Override
    public boolean isVirgin() {
        return false;
    }

    @Override
    public void setList(List<DataItem> inputList) {

    }

    @Override
    public void setList(List<DataItem> inputList, CharSequence filterQuery) {

    }

    @Override
    public void appendList(List<DataItem> tagsList) {

    }

    @Override
    public DataItem getItem(int position) {
        return null;
    }

    @Override
    public List<DataItem> getAllItems() {
        return null;
    }

    @Override
    public void removeItem(DataItem dataItem) {

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
    public int getPositionOf(DataItem dataItem) {
        return 0;
    }

    @Override
    public boolean allItemsAreSelected() {
        return false;
    }

    @Override
    public Filter getFilter() {
        return null;
    }

    @Override
    public boolean isSelected(Integer index) {
        return false;
    }

    @Override
    public boolean isMultipleItemsSelected() {
        return false;
    }

    @Override
    public int getSelectedItemCount() {
        return 0;
    }

    @Override
    public Integer getSingleSelectedItemIndex() {
        return null;
    }

    @Override
    public List<Integer> getSelectedIndexes() {
        return null;
    }

    @Override
    public void toggleSelection(int itemIndex) {

    }

    @Override
    public void selectAll(int listSize) {

    }

    @Override
    public void clearSelection() {

    }
}
