package ru.aakumykov.me.sociocat.template_of_list.list_items;

import ru.aakumykov.me.sociocat.template_of_list.iTemplateOfList;

public abstract class ListItem {

    public abstract int compareTo(ListItem listItem);

    public abstract iTemplateOfList.ItemType getItemType();

    public abstract void setIsNowDeleting(boolean value);

    public abstract boolean isNowDeleting();
}
