package ru.aakumykov.me.sociocat.a_basic_mvp_list_components.interfaces;


import java.util.List;

import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.list_items.BasicMVP_DataItem;

public interface iBasicSelectableList {

    boolean isSelectionMode();

    void toggleItemSelection(int position);

    Integer getSelectedItemsCount();
    List<BasicMVP_DataItem> getSelectedItems();

    void selectAll();
    void clearSelection();
    void invertSelection();
}
