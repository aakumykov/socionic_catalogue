package ru.aakumykov.me.sociocat.template_of_list.model;

import ru.aakumykov.me.sociocat.template_of_list.iItemsList;

public class LoadMoreItem extends ListItem {

    @Override
    public int compareTo(ListItem listItem) {
        if (listItem instanceof DataItem)
            return -1;
        else
            return 1;
    }

    @Override
    public iItemsList.ItemType getItemType() {
        return iItemsList.ItemType.LOADMORE_ITEM;
    }
}
