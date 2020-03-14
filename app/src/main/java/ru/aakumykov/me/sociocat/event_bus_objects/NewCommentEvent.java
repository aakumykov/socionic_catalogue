package ru.aakumykov.me.sociocat.event_bus_objects;

public class NewCommentEvent {
    private String commentKey;
    private String text;
    private String userId;
    private String userName;
    private String cardId;
    private String cardTitle;

    public NewCommentEvent() {}

    public void setCommentKey(String commentKey) {
        this.commentKey = commentKey;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public void setCardTitle(String cardTitle) {
        this.cardTitle = cardTitle;
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

    public String getCardTitle() {
        return cardTitle;
    }
}
