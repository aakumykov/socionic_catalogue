package ru.aakumykov.me.sociocat.template_of_list.stubs;

import android.widget.Filter;

import java.util.List;

import ru.aakumykov.me.sociocat.template_of_list.iTemplateOfList;
import ru.aakumykov.me.sociocat.template_of_list.list_items.DataItem;
import ru.aakumykov.me.sociocat.template_of_list.list_items.ListItem;

public class TemplateOfList_DataAdapter_Stub
        extends SelectableAdapter_Stub
        implements iTemplateOfList.iDataAdapter
{

    @Override
    public void bindBottomReachedListener(iTemplateOfList.ListEdgeReachedListener listener) {

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
    public List<DataItem> getAllDataItems() {
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
    public int getDataItemsCount() {
        return 0;
    }

    @Override
    public void sortByName(iTemplateOfList.SortingListener sortingListener) {

    }

    @Override
    public void sortByCount(iTemplateOfList.SortingListener sortingListener) {

    }

    @Override
    public iTemplateOfList.SortingMode getSortingMode() {
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
    public void setLayoutMode(iTemplateOfList.LayoutMode currentLayoutMode) {

    }

    @Override
    public void setItemIsNowDeleting(DataItem dataItem, boolean value) {

    }

    @Override
    public Filter getFilter() {
        return null;
    }
}
