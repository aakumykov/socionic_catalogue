package ru.aakumykov.me.sociocat.template_of_list;

import java.util.List;

import ru.aakumykov.me.sociocat.template_of_list.model.Item;

interface iSelectableAdapter {
    boolean isSelected(int position);

    int getSelectedItemCount();

    List<Integer> getSelectedItems();

    void toggleSelection(int position);

    void clearSelection();
}
