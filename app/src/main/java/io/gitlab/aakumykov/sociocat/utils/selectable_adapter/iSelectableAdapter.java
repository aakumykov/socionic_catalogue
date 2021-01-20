package io.gitlab.aakumykov.sociocat.utils.selectable_adapter;

import java.util.List;

public interface iSelectableAdapter {

    boolean isSelected(Integer index);

    boolean isMultipleItemsSelected();

    int getSelectedItemsCount();

    boolean isSingleItemSelected();

    List<Integer> getSelectedIndexes();

    void toggleSelection(int itemIndex);

    void selectAll(int listSize);

    void clearSelection();
}
