package ru.aakumykov.me.sociocat.cards_grid_3.items;

public interface iGridItem {

    enum ItemType {
        CARD_ITEM_TYPE,
        LOAD_MORE_ITEM_TYPE
    }

    int CARD_VIEW_TYPE = 10;
    int LOAD_MORE_VIEW_TYPE = 20;
}
