package ru.aakumykov.me.sociocat.a_basic_mvp_components.interfaces;

import java.util.List;

import ru.aakumykov.me.sociocat.a_basic_mvp_components.enums.eSortingOrder;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.list_Items.BasicMVP_DataItem;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.list_Items.BasicMVP_ListItem;


public interface iBasicList extends iBasicFilterableLsit, iBasicSelectableList
{
    boolean isVirgin();

    void setList(List<BasicMVP_ListItem> inputList);
    void setListAndFilter(List<BasicMVP_ListItem> list);

    void appendList(List<BasicMVP_ListItem> inputList);
    void appendListAndSort(List<BasicMVP_ListItem> inputList, iSortingMode sortingMode, eSortingOrder sortingOrder);
    void appendListAndFilter(List<BasicMVP_ListItem> inputList);

    void addItem(BasicMVP_ListItem item);
    void removeItem(BasicMVP_ListItem item);
    void updateItemInVisibleList(int position, BasicMVP_ListItem item);
    void updateItemInOriginalList(int position, BasicMVP_ListItem item);

    int getAllItemsCount();
    int getDataItemsCount();

    int findVisibleObjectPosition(iComparisionCallback callback);
    int findOriginalObjectPosition(iComparisionCallback callback);

    BasicMVP_ListItem getItem(int position);
    BasicMVP_DataItem getLastDataItem();

    void refreshItem(int position);

    void showThrobberItem();
    void hideThrobberItem();

    void showLoadmoreItem();
    void showLoadmoreItem(int titleId);

    void hideLoadmoreItem();

    void sortList(iSortingMode sortingMode, eSortingOrder sortingOrder);
    boolean isSorted();


    interface iComparisionCallback {
        boolean onCompare(Object objectFromList);
    }
}
