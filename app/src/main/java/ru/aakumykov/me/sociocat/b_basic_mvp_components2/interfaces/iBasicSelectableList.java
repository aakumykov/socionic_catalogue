package ru.aakumykov.me.sociocat.b_basic_mvp_components2.interfaces;


import java.util.List;

import ru.aakumykov.me.sociocat.b_basic_mvp_components2.list_Items.BasicMVP_DataItem;

public interface iBasicSelectableList {

    boolean isSelectionMode();

    void toggleItemSelection(int position);

    Integer getSelectedItemsCount();
    List<BasicMVP_DataItem> getSelectedItems();

    void selectAll();
    void clearSelection();
    void invertSelection();
}
