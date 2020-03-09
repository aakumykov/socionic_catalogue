package ru.aakumykov.me.sociocat.event_bus_objects;

import ru.aakumykov.me.sociocat.models.User;

public class UserAuthorizedEvent {

    private User user;

    public UserAuthorizedEvent(User user) {
        this.user = user;
    }

    public User getUser() {
        return this.user;
    }
}
