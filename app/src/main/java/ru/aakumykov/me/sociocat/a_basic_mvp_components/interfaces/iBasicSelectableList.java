package ru.aakumykov.me.sociocat.a_basic_mvp_components.interfaces;

import java.util.List;

import ru.aakumykov.me.sociocat.a_basic_mvp_components.list_Items.BasicMVP_DataItem;

public interface iBasicSelectableList {

    boolean isSelectionMode();

    void toggleItemSelection(int position);

    Integer getSelectedItemsCount();

    void selectAll();
    void clearSelection();
    void invertSelection();

    List<BasicMVP_DataItem> getSelectedItems();
}
