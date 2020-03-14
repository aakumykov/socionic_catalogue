package ru.aakumykov.me.sociocat.event_bus_objects;

public class NewCommentEvent {
    private String commentKey;
    private String text;
    private String userName;
    private String cardId;

    public NewCommentEvent(String commentKey, String text, String userName, String cardId) {
        this.commentKey = commentKey;
        this.text = text;
        this.userName = userName;
        this.cardId = cardId;
    }

    public String getCommentKey() {
        return commentKey;
    }

    public void setCommentKey(String commentKey) {
        this.commentKey = commentKey;
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
