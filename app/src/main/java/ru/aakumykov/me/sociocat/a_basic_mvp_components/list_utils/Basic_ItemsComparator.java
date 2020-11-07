package ru.aakumykov.me.sociocat.a_basic_mvp_components.list_utils;

import android.util.Log;

import ru.aakumykov.me.sociocat.a_basic_mvp_components.enums.eBasic_SortingMode;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.enums.eSortingOrder;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.interfaces.iBasicData;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.interfaces.iItemsComparator;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.interfaces.iListBottomItem;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.interfaces.iListTopItem;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.interfaces.iSortingMode;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.list_Items.Basic_DataItem;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.list_Items.Basic_ListItem;


public class Basic_ItemsComparator implements iItemsComparator {

    private static final String TAG = Basic_ItemsComparator.class.getSimpleName();
    protected iSortingMode mSortingMode;
    protected eSortingOrder mSortingOrder;


    @Override
    public void setSortingMode(iSortingMode sortingMode, eSortingOrder sortingOrder) {
        mSortingMode = sortingMode;
        mSortingOrder = sortingOrder;
    }

    @Override
    public int compare(Basic_ListItem o1, Basic_ListItem o2) {

        if (hasTopItem(o1, o2)) {
            return sortTopItems(o1 ,o2);
        }
        else if (hasBottomItem(o1, o2)) {
            return sortBottomItems(o1 ,o2);
        }
        else {
            return sortMiddleItems(o1, o2);
        }
    }


    protected boolean hasPinnedItems(Basic_ListItem o1, Basic_ListItem o2) {
        return hasTopItem(o1, o2) || hasBottomItem(o1, o2);
    }

    protected int sortPinnedItems(Basic_ListItem o1, Basic_ListItem o2) {
        if (hasTopItem(o1, o2))
            return sortTopItems(o1 ,o2);

        else if (hasBottomItem(o1, o2))
            return sortBottomItems(o1 ,o2);

        else {
            Log.e(TAG, "Requested to sort pinned item, but has no one");
            return 0;
        }
    }

    protected int sortTopItems(Basic_ListItem o1, Basic_ListItem o2) {
        return 0;
    }

    protected int sortBottomItems(Basic_ListItem o1, Basic_ListItem o2) {
        return 0;
    }

    protected int sortMiddleItems(Basic_ListItem o1, Basic_ListItem o2) {

        if (mSortingMode instanceof eBasic_SortingMode) {

            switch ((eBasic_SortingMode) mSortingMode) {
                case BY_NAME:
                    return sortByName(o1, o2);

                case BY_DATE:
                    return sortByDate(o1, o2);

                default:
                    return unknownSortingMode(TAG, mSortingMode);
            }
        }
        else {
            Log.e(TAG, "Sorting mode is not eBasic_SortingMode instance: " + mSortingMode);
            return 0;
        }
    }

    protected boolean hasTopItem(Basic_ListItem item1, Basic_ListItem item2) {
        return isTopItem(item1) || isTopItem(item2);
    }

    protected boolean hasBottomItem(Basic_ListItem item1, Basic_ListItem item2) {
        return isBottomItem(item1) || isBottomItem(item2);
    }

    protected boolean isTopItem(Basic_ListItem listItem) {
        return listItem instanceof iListTopItem;
    }

    protected boolean isBottomItem(Basic_ListItem listItem) {
        return listItem instanceof iListBottomItem;
    }

    protected int unknownSortingMode(String tag, iSortingMode sortingMode) {
        Log.e(tag, "Неизвестный режим сортировки: "+sortingMode);
        return 0;
    }



    private int sortByName(Basic_ListItem o1, Basic_ListItem o2) {
        Basic_DataItem dataItem1 = (Basic_DataItem) o1;
        Basic_DataItem dataItem2 = (Basic_DataItem) o2;

        iBasicData data1 = (iBasicData) dataItem1.getPayload();
        iBasicData data2 = (iBasicData) dataItem2.getPayload();

        String name1 = data1.getName();
        String name2 = data2.getName();

        if (mSortingOrder.isDirect())
            return name1.compareTo(name2);
        else
            return name2.compareTo(name1);
    }

    private int sortByDate(Basic_ListItem o1, Basic_ListItem o2) {
        Basic_DataItem dataItem1 = (Basic_DataItem) o1;
        Basic_DataItem dataItem2 = (Basic_DataItem) o2;

        iBasicData basicData1 = dataItem1.getBasicData();
        iBasicData basicData2 = dataItem2.getBasicData();

        Long date1 = basicData1.getDate();
        Long date2 = basicData2.getDate();

        if (mSortingOrder.isDirect())
            return date1.compareTo(date2);
        else
            return date2.compareTo(date1);
    }

}
