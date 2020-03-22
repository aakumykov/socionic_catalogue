package ru.aakumykov.me.sociocat.cards_list.stubs;

import java.util.List;

import ru.aakumykov.me.sociocat.cards_list.selectable_adapter.iSelectableAdapter;

public abstract class SelectableAdapter_Stub implements iSelectableAdapter {
    @Override
    public boolean isSelected(Integer index) {
        return false;
    }

    @Override
    public boolean isMultipleItemsSelected() {
        return false;
    }

    @Override
    public int getSelectedItemCount() {
        return 0;
    }

    @Override
    public Integer getSingleSelectedItemIndex() {
        return null;
    }

    @Override
    public List<Integer> getSelectedIndexes() {
        return null;
    }

    @Override
    public void toggleSelection(int itemIndex) {

    }

    @Override
    public void selectAll(int listSize) {

    }

    @Override
    public void clearSelection() {

    }
}
