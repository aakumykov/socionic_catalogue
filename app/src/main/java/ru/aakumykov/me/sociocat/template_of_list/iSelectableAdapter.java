package ru.aakumykov.me.sociocat.template_of_list;

import java.util.List;

import ru.aakumykov.me.sociocat.template_of_list.model.Item;

public interface iSelectableAdapter {

    boolean isSelected(Item item);

    boolean isMultipleItemsSelected();

    int getSelectedItemCount();

    Item getSingleSelectedItem();

    List<Item> getSelectedItems();

    void toggleSelection(Item item, int itemIndex);

    void selectItemsList(List<Item> itemList);

    void clearSelection();
}
