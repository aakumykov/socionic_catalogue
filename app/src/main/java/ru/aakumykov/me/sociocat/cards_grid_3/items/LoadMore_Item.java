package ru.aakumykov.me.sociocat.cards_grid_3.items;

import ru.aakumykov.me.sociocat.models.Card;

public class LoadMore_Item implements iGridItem {

    private String startKey;

    public LoadMore_Item(Card nextFirstCard) {
        this.startKey = nextFirstCard.getKey();
    }

    public String getStartKey() {
        return startKey;
    }
}
