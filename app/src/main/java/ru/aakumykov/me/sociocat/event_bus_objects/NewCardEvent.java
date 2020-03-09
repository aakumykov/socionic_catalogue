package ru.aakumykov.me.sociocat.event_bus_objects;

import ru.aakumykov.me.sociocat.models.Card;

public class NewCardEvent {
    private Card card;

    public NewCardEvent(Card card) {
        this.card = card;
    }

    public Card getCard() {
        return card;
    }
}
