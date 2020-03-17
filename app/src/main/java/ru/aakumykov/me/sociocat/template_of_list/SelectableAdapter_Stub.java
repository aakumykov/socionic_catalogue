package ru.aakumykov.me.sociocat.template_of_list;

import java.util.List;

public abstract class SelectableAdapter_Stub implements iSelectableAdapter {
    @Override
    public boolean isSelected(int position) {
        return false;
    }

    @Override
    public int getSelectedItemCount() {
        return 0;
    }

    @Override
    public List<Integer> getSelectedItems() {
        return null;
    }

    @Override
    public void toggleSelection(int position) {

    }

    @Override
    public void clearSelection() {

    }
}
