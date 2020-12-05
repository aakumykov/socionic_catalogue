package ru.aakumykov.me.sociocat.a_basic_mvp_list_components.interfaces;


import androidx.annotation.NonNull;

import java.util.List;

import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.enums.eSortingOrder;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.list_items.BasicMVPList_DataItem;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.list_items.BasicMVPList_ListItem;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.view_modes.BasicViewMode;

public interface iBasicList extends iBasicFilterableLsit, iBasicSelectableList
{
    boolean isVirgin();

    void setList(List<BasicMVPList_ListItem> inputList);
    void setOriginalList(List<BasicMVPList_ListItem> list);
    void setListAndFilter(List<BasicMVPList_ListItem> list);

    void appendList(List<BasicMVPList_ListItem> inputList);
    void appendListAndSort(List<BasicMVPList_ListItem> inputList, iSortingMode sortingMode, eSortingOrder sortingOrder);
    void appendListAndFilter(List<BasicMVPList_ListItem> inputList);

    void appendItem(BasicMVPList_ListItem item);
    void insertItem(int position, BasicMVPList_ListItem item);
    void removeItem(BasicMVPList_ListItem item);

    iItemsComparator getItemsComparator(iSortingMode sortingMode, eSortingOrder sortingOrder);

    int getVisibleItemsCount();

    int findVisibleObjectPosition(iFindItemComparisionCallback callback);
    int findOriginalObjectPosition(iFindItemComparisionCallback callback);

    int getVisibleDataItemsCount();

    int getOriginalDataItemsCount();

    List<BasicMVPList_DataItem> getVisibleDataItems();

    BasicMVPList_ListItem getItem(int position);
    BasicMVPList_DataItem getLastDataItem();
    BasicMVPList_DataItem getLastUnfilteredDataItem();

    void refreshItem(int position);

    int updateItemInList(@NonNull BasicMVPList_DataItem newItem,
                         @NonNull iFindItemComparisionCallback comparisionCallback);

    void deleteItemFromList(@NonNull iFindItemComparisionCallback comparisionCallback);

    void showThrobberItem();
    void hideThrobberItem();

    void showLoadmoreItem();
    void showLoadmoreItem(int titleId);

    void hideLoadmoreItem();

    void sortCurrentList(iSortingMode sortingMode, eSortingOrder sortingOrder);
    List<BasicMVPList_ListItem> applyCurrentSortingToList(List<BasicMVPList_ListItem> inputList);

    boolean isSorted();

    void highlightItem(int position);

    void setViewMode(BasicViewMode viewMode);

    interface iFindItemComparisionCallback {
        boolean onCompareWithListItemPayload(Object itemPayload);
    }
}
