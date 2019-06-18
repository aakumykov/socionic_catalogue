package ru.aakumykov.me.sociocat.cards_grid_3.items;

import ru.aakumykov.me.sociocat.models.Card;

public class LoadMore_Item extends GridItem {

    public String getStartKey() {
        return ((Card) getPayload()).getKey();
    }
}
