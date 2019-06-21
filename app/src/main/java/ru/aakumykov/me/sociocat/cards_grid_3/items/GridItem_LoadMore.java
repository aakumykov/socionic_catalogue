package ru.aakumykov.me.sociocat.cards_grid_3.items;

import ru.aakumykov.me.sociocat.models.Card;

public class GridItem_LoadMore extends GridItem {

    public String getStartKey() {
        return ((Card) getPayload()).getKey();
    }
}
