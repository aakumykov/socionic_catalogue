package ru.aakumykov.me.sociocat.cards_list.stubs;

import android.widget.Filter;

import java.util.List;

import ru.aakumykov.me.sociocat.cards_list.iCardsList;
import ru.aakumykov.me.sociocat.cards_list.list_items.DataItem;
import ru.aakumykov.me.sociocat.cards_list.list_items.ListItem;

public class CardsList_DataAdapter_Stub
        extends SelectableAdapter_Stub
        implements iCardsList.iDataAdapter
{
    @Override
    public void bindBottomReachedListener(iCardsList.ListEdgeReachedListener listener) {

    }

    @Override
    public void unbindBottomReachedListener() {

    }

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
    public void appendList(List<DataItem> inputList) {

    }

    @Override
    public DataItem getDataItem(int position) {
        return null;
    }

    @Override
    public List<ListItem> getAllItems() {
        return null;
    }

    @Override
    public DataItem getLastDataItem() {
        return null;
    }

    @Override
    public void removeItem(ListItem listItem) {

    }

    @Override
    public int getListSize() {
        return 0;
    }

    @Override
    public void sortByName(iCardsList.SortingListener sortingListener) {

    }

    @Override
    public void sortByCount(iCardsList.SortingListener sortingListener) {

    }

    @Override
    public iCardsList.SortingMode getSortingMode() {
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
    public void showLoadmoreItem() {

    }

    @Override
    public void showThrobberItem() {

    }

    @Override
    public void hideLoadmoreItem() {

    }

    @Override
    public void hideThrobberItem() {

    }

    @Override
    public List<DataItem> getSelectedItems() {
        return null;
    }

    @Override
    public void setViewMode(iCardsList.ViewMode currentViewMode) {

    }

    @Override
    public Filter getFilter() {
        return null;
    }
}
