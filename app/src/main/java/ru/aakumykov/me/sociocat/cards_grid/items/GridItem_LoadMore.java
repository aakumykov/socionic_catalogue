package ru.aakumykov.me.sociocat.cards_grid.items;

import ru.aakumykov.me.sociocat.models.Card;

public class GridItem_LoadMore extends GridItem {

    public String getStartKey() {
        return ((Card) getPayload()).getKey();
    }
}
