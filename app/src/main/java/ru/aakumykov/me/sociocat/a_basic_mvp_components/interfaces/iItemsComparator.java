package ru.aakumykov.me.sociocat.a_basic_mvp_components.interfaces;

import java.util.Comparator;

import ru.aakumykov.me.sociocat.a_basic_mvp_components.enums.eSortingOrder;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.list_Items.BasicMVP_ListItem;


public interface iItemsComparator extends Comparator<BasicMVP_ListItem> {
    void setSortingMode(iSortingMode sortingMode, eSortingOrder sortingOrder);
}
