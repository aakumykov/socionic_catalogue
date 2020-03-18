package ru.aakumykov.me.sociocat.template_of_list.stubs;

import java.util.List;

import ru.aakumykov.me.sociocat.template_of_list.iSelectableAdapter;
import ru.aakumykov.me.sociocat.template_of_list.model.Item;

public abstract class SelectableAdapter_Stub implements iSelectableAdapter {
    @Override
    public boolean isSelected(Item item) {
        return false;
    }

    @Override
    public int getSelectedItemCount() {
        return 0;
    }

    @Override
    public List<Item> getSelectedItems() {
        return null;
    }

    @Override
    public void toggleSelection(Item item, int itemIndex) {

    }

    @Override
    public void clearSelection() {

    }
}
