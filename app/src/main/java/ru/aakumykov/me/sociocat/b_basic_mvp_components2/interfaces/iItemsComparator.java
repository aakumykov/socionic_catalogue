package ru.aakumykov.me.sociocat.b_basic_mvp_components2.interfaces;


import java.util.Comparator;

import ru.aakumykov.me.sociocat.b_basic_mvp_components2.enums.eSortingOrder;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.list_Items.BasicMVP_ListItem;

public interface iItemsComparator extends Comparator<BasicMVP_ListItem> {
    void setSortingMode(iSortingMode sortingMode, eSortingOrder sortingOrder);
}
