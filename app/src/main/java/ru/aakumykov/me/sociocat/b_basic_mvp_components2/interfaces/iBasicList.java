package ru.aakumykov.me.sociocat.b_basic_mvp_components2.interfaces;


import androidx.annotation.NonNull;

import java.util.List;

import ru.aakumykov.me.sociocat.b_basic_mvp_components2.enums.eSortingOrder;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.list_items.BasicMVP_DataItem;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.list_items.BasicMVP_ListItem;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.view_modes.BasicViewMode;

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

    int getVisibleItemsCount();

    int findVisibleObjectPosition(iFindItemComparisionCallback callback);
    int findOriginalObjectPosition(iFindItemComparisionCallback callback);

    int getVisibleDataItemsCount();

    int getOriginalDataItemsCount();

    List<BasicMVP_DataItem> getVisibleDataItems();

    BasicMVP_ListItem getItem(int position);
    BasicMVP_DataItem getLastDataItem();
    BasicMVP_DataItem getLastUnfilteredDataItem();

    void refreshItem(int position);

    int updateItemInList(@NonNull BasicMVP_DataItem newItem,
                         @NonNull iFindItemComparisionCallback comparisionCallback);

    void showThrobberItem();
    void hideThrobberItem();

    void showLoadmoreItem();
    void showLoadmoreItem(int titleId);

    void hideLoadmoreItem();

    void sortList(iSortingMode sortingMode, eSortingOrder sortingOrder);
    boolean isSorted();

    void highlightItem(int position);

    void setViewMode(BasicViewMode viewMode);


    interface iFindItemComparisionCallback {
        boolean onCompareFindingOldItemPosition(Object objectFromList);
    }
}
