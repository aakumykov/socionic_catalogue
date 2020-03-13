package ru.aakumykov.me.sociocat.event_bus_objects;

public class NewCardEvent {
    private String cardKey;
    private String cardTitle;
    private String cardAuthor;

    public NewCardEvent() {
    }

    public NewCardEvent(String cardKey, String cardTitle, String cardAuthor) {
        this.cardKey = cardKey;
        this.cardTitle = cardTitle;
        this.cardAuthor = cardAuthor;
    }

    public void setCardKey(String cardKey) {
        this.cardKey = cardKey;
    }

    public void setCardTitle(String cardTitle) {
        this.cardTitle = cardTitle;
    }

    public void setCardAuthor(String cardAuthor) {
        this.cardAuthor = cardAuthor;
    }

    public String getCardKey() {
        return cardKey;
    }

    public String getCardTitle() {
        return cardTitle;
    }

    public String getCardAuthor() {
        return cardAuthor;
    }
}
