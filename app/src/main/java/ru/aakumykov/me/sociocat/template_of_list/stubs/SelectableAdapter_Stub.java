package ru.aakumykov.me.sociocat.template_of_list.stubs;

import java.util.List;

import ru.aakumykov.me.sociocat.template_of_list.iSelectableAdapter;

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
