package ru.aakumykov.me.sociocat.a_basic_mvp_components.interfaces;

public interface iBasicSelectableList {

    boolean isSelectionMode();

    void toggleItemSelection(int position);

    Integer getSelectedItemsCount();
    void resetSelectionCounter();

    void selectAll();
    void clearSelection();
    void invertSelection();
}
