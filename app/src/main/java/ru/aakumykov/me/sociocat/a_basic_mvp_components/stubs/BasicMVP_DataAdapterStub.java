package ru.aakumykov.me.sociocat.a_basic_mvp_components.stubs;

import android.widget.Filter;

import java.util.List;

import ru.aakumykov.me.sociocat.a_basic_mvp_components.enums.eSortingOrder;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.interfaces.iBasicList;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.interfaces.iSortingMode;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.list_Items.BasicMVP_DataItem;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.list_Items.BasicMVP_ListItem;


public class BasicMVP_DataAdapterStub implements iBasicList {

    @Override
    public boolean isVirgin() {
        return false;
    }

    @Override
    public void setList(List<BasicMVP_ListItem> inputList) {

    }

    @Override
    public void setListAndFilter(List<BasicMVP_ListItem> list) {

    }

    @Override
    public void appendList(List<BasicMVP_ListItem> inputList) {

    }

    @Override
    public void appendListAndSort(List<BasicMVP_ListItem> inputList, iSortingMode sortingMode, eSortingOrder sortingOrder) {

    }

    @Override
    public void appendListAndFilter(List<BasicMVP_ListItem> inputList) {

    }

    @Override
    public void addItem(BasicMVP_ListItem item) {

    }

    @Override
    public int getAllItemsCount() {
        return 0;
    }

    @Override
    public int getDataItemsCount() {
        return 0;
    }

    @Override
    public BasicMVP_ListItem getItem(int position) {
        return null;
    }

    @Override
    public BasicMVP_DataItem getLastDataItem() {
        return null;
    }

    @Override
    public void refreshItem(int position) {

    }

    @Override
    public void showThrobberItem() {

    }

    @Override
    public void hideThrobberItem() {

    }

    @Override
    public void showLoadmoreItem() {

    }

    @Override
    public void showLoadmoreItem(int titleId) {

    }

    @Override
    public void hideLoadmoreItem() {

    }

    @Override
    public void sortList(iSortingMode sortingMode, eSortingOrder sortingOrder) {

    }

    @Override
    public boolean isSelectionMode() {
        return false;
    }

    @Override
    public void toggleItemSelection(int position) {

    }

    @Override
    public Integer getSelectedItemsCount() {
        return 0;
    }

    @Override
    public void selectAll() {

    }

    @Override
    public void clearSelection() {

    }

    @Override
    public void invertSelection() {

    }

    @Override
    public void resetSelectionCounter() {

    }

    @Override
    public boolean isSorted() {
        return false;
    }

    @Override
    public Filter getFilter() {
        return null;
    }

    @Override
    public void prepareFilter() {

    }

    @Override
    public void removeFilter() {

    }

    @Override
    public String getFilterText() {
        return null;
    }

    @Override
    public void filterItems(String pattern) {

    }

    @Override
    public boolean isFiltered() {
        return false;
    }
}
