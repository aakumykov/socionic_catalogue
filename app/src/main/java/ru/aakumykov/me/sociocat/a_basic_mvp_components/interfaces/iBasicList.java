package ru.aakumykov.me.sociocat.a_basic_mvp_components.interfaces;

import java.util.List;

import ru.aakumykov.me.sociocat.a_basic_mvp_components.enums.eSortingOrder;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.list_Items.Basic_DataItem;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.list_Items.Basic_ListItem;


public interface iBasicList extends iBasicFilterableLsit, iBasicSelectableList
{
    boolean isVirgin();

    void setList(List<Basic_ListItem> inputList);
    void setListAndFilter(List<Basic_ListItem> list);

    void appendList(List<Basic_ListItem> inputList);
    void appendListAndSort(List<Basic_ListItem> inputList, iSortingMode sortingMode, eSortingOrder sortingOrder);
    void appendListAndFilter(List<Basic_ListItem> inputList);

    void addItem(Basic_ListItem item);

    int getAllItemsCount();
    int getDataItemsCount();

    Basic_ListItem getItem(int position);
    Basic_DataItem getLastDataItem();

    void refreshItem(int position);

    void showThrobberItem();
    void hideThrobberItem();

    void showLoadmoreItem();
    void showLoadmoreItem(int titleId);

    void hideLoadmoreItem();

    void sortList(iSortingMode sortingMode, eSortingOrder sortingOrder);
    boolean isSorted();
}
