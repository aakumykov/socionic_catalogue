package ru.aakumykov.me.sociocat.b_basic_mvp_components2.interfaces;


import java.util.List;

import ru.aakumykov.me.sociocat.b_basic_mvp_components2.enums.eSortingOrder;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.list_items.BasicMVP_DataItem;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.list_items.BasicMVP_ListItem;

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

    int getVisibleItemsCount();

    int findVisibleObjectPosition(iComparisionCallback callback);
    int findOriginalObjectPosition(iComparisionCallback callback);

    int getVisibleDataItemsCount();

    int getOriginalDataItemsCount();

    List<BasicMVP_DataItem> getVisibleDataItems();

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

    void highlightItem(int position);

    void setViewMode(iViewMode viewMode);


    interface iComparisionCallback {
        boolean onCompare(Object objectFromList);
    }
}
