package ru.aakumykov.me.sociocat.cards_list.list_items;

import ru.aakumykov.me.sociocat.cards_list.iCardsList;

public class ThrobberItem extends ListItem {

    @Override
    public int compareTo(ListItem listItem) {
        if (listItem instanceof DataItem)
            return -1;
        else
            return 1;
    }

    @Override
    public iCardsList.ItemType getItemType() {
        return iCardsList.ItemType.THROBBER_ITEM;
    }

    @Override
    public void setIsNowDeleting(boolean value) {

    }

    @Override
    public boolean isNowDeleting() {
        return false;
    }
}
