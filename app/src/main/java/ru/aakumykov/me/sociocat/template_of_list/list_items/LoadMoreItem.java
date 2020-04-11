package ru.aakumykov.me.sociocat.template_of_list.list_items;

import ru.aakumykov.me.sociocat.template_of_list.iTemplateOfList;

public class LoadMoreItem extends ListItem {

    @Override
    public int compareTo(ListItem listItem) {
        if (listItem instanceof DataItem)
            return -1;
        else
            return 1;
    }

    @Override
    public iTemplateOfList.ItemType getItemType() {
        return iTemplateOfList.ItemType.LOADMORE_ITEM;
    }

    @Override
    public void setIsNowDeleting(boolean value) {

    }

    @Override
    public boolean isNowDeleting() {
        return false;
    }
}
