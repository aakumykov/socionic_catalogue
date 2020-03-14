package ru.aakumykov.me.sociocat.event_bus_objects;

public class NewCommentEvent {
    private String commentKey;
    private String text;
    private String userId;
    private String userName;
    private String cardId;

    public NewCommentEvent(String commentKey, String text, String userId, String userName, String cardId) {
        this.commentKey = commentKey;
        this.text = text;
        this.userId = userId;
        this.userName = userName;
        this.cardId = cardId;
    }

    public String getCommentKey() {
        return commentKey;
    }

    public String getText() {
        return text;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getCardId() {
        return cardId;
    }
}
