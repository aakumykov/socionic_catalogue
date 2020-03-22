package ru.aakumykov.me.sociocat.utils.selectable_adapter;

import java.util.List;

public interface iSelectableAdapter {

    boolean isSelected(Integer index);

    boolean isMultipleItemsSelected();

    int getSelectedItemCount();

    Integer getSingleSelectedItemIndex();

    List<Integer> getSelectedIndexes();

    void toggleSelection(int itemIndex);

    void selectAll(int listSize);

    void clearSelection();
}
