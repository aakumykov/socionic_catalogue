package ru.aakumykov.me.sociocat.cards_list.list_items;

import ru.aakumykov.me.sociocat.cards_list.iItemsList;

public class ThrobberItem extends ListItem {

    @Override
    public int compareTo(ListItem listItem) {
        if (listItem instanceof DataItem)
            return -1;
        else
            return 1;
    }

    @Override
    public iItemsList.ItemType getItemType() {
        return iItemsList.ItemType.THROBBER_ITEM;
    }
}
