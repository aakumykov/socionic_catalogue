package io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.interfaces;


import java.util.List;

import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.list_items.BasicMVPList_DataItem;

public interface iBasicSelectableList {

    boolean isSelectionMode();

    void toggleItemSelection(int position);

    Integer getSelectedItemsCount();
    List<BasicMVPList_DataItem> getSelectedItems();

    void selectAll();
    void clearSelection();
    void invertSelection();
}
