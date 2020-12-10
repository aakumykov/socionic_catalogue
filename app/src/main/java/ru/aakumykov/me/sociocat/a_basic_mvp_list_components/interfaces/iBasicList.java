package ru.aakumykov.me.sociocat.a_basic_mvp_list_components.interfaces;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.enums.eSortingOrder;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.list_items.BasicMVPList_DataItem;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.list_items.BasicMVPList_ListItem;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.list_utils.BasicMVPList_ItemsTextFilter;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.view_modes.BasicViewMode;

public interface iBasicList extends iBasicFilterableLsit, iBasicSelectableList
{
    boolean isVirgin();

    void setList(List<BasicMVPList_ListItem> inputList);
    void setList(List<BasicMVPList_ListItem> inputList, @Nullable iSetListCallback callback);

    void appendList(List<BasicMVPList_ListItem> inputList);
    void appendListAndSort(List<BasicMVPList_ListItem> inputList, iSortingMode sortingMode, eSortingOrder sortingOrder);
    void appendListAndFilter(List<BasicMVPList_ListItem> inputList);

    void appendItem(BasicMVPList_ListItem item);
    void insertItem(int position, BasicMVPList_ListItem item);
    void removeItem(BasicMVPList_ListItem item);

    int findAndUpdateItem(@NonNull BasicMVPList_DataItem newItem, @NonNull iFindItemComparisionCallback comparisionCallback);
    void findAndRemoveItem(@NonNull iFindItemComparisionCallback comparisionCallback);


    iItemsComparator getItemsComparator(iSortingMode sortingMode, eSortingOrder sortingOrder);

    int getVisibleItemsCount(); // инкапсулировать в прокрутку

    // TODO: iFindItemComparisionCallback --> Comparator
    int findVisibleObjectPosition(iFindItemComparisionCallback callback); // инкапсулировать
    int findOriginalObjectPosition(iFindItemComparisionCallback callback); // инкапсулировать

    int getVisibleDataItemsCount(); // инкапсулировать в "все выбраны?"
    int getOriginalDataItemsCount();

    List<BasicMVPList_DataItem> getVisibleDataItems();

    BasicMVPList_ListItem getItem(int position);
    BasicMVPList_DataItem getTailDataItem();

    void refreshItem(int position); // сделать внутренним

    void showThrobberItem();
    void hideThrobberItem();

    void showLoadmoreItem();
    void showLoadmoreItem(int titleId);
    void hideLoadmoreItem();


    // TODO: переименовать в sortList()
    void sortList(iSortingMode sortingMode, eSortingOrder sortingOrder);
    boolean isSorted();

//    <T> void filterList(Comparator<T> comparator);
//    boolean isFiltered();

    List<BasicMVPList_ListItem> applyCurrentSortingToList(List<BasicMVPList_ListItem> inputList);

    void highlightItem(int position);

    void setViewMode(BasicViewMode viewMode);

    int getVisibleListSize();

    void setTextFilter(BasicMVPList_ItemsTextFilter predicate);
    void removeTextFilter();
    void filterList(String pattern);
    boolean isFiltered();


    interface iFindItemComparisionCallback {
        boolean onCompareWithListItemPayload(Object itemPayload);
    }

    interface iSetListCallback {
        void onListSet(int allItemsCount, int addedItemsCount, int filteredOutItemsCount);
    }
}
