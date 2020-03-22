package ru.aakumykov.me.sociocat.cards_list.list_items;

import ru.aakumykov.me.sociocat.cards_list.iCardsList;

public abstract class ListItem {

    public abstract int compareTo(ListItem listItem);

    public abstract iCardsList.ItemType getItemType();
}
