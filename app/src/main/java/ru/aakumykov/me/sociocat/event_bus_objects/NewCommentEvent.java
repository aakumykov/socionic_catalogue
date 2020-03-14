package ru.aakumykov.me.sociocat.event_bus_objects;

public class NewCommentEvent {
    private String key;
    private String text;
    private String userName;
    private String cardId;

    public NewCommentEvent(String key, String text, String userName, String cardId) {
        this.key = key;
        this.text = text;
        this.userName = userName;
        this.cardId = cardId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }
}
