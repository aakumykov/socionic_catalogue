package ru.aakumykov.me.sociocat.event_bus_objects;

public class NewCardEvent {
    private String cardKey;

    public NewCardEvent(String cardKey) {
        this.cardKey = cardKey;
    }

    public String getCardKey() {
        return cardKey;
    }
}
