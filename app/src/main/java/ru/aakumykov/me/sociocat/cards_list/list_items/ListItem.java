package ru.aakumykov.me.sociocat.cards_list.list_items;

import ru.aakumykov.me.sociocat.cards_list.iItemsList;

public abstract class ListItem {

    public abstract int compareTo(ListItem listItem);

    public abstract iItemsList.ItemType getItemType();
}
