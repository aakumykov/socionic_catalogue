package ru.aakumykov.me.sociocat.template_of_list.model;

import ru.aakumykov.me.sociocat.template_of_list.iItemsList;

public abstract class ListItem {

    public abstract int compareTo(ListItem listItem);

    public abstract iItemsList.ItemType getItemType();
}
