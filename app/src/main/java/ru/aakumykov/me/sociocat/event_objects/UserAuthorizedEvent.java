package ru.aakumykov.me.sociocat.event_objects;

public class UserAuthorizedEvent {

    private String uid;

    public UserAuthorizedEvent(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return this.uid;
    }
}
