package ru.aakumykov.me.sociocat.cards_grid.items;

import ru.aakumykov.me.sociocat.models.Card;

public class GridItem_Card extends GridItem {

    public GridItem_Card() {
    }

    public GridItem_Card(Card card) {
        setPayload(card);
    }
}
