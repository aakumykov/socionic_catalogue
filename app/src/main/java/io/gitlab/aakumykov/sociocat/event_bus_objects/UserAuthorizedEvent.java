package io.gitlab.aakumykov.sociocat.event_bus_objects;

import io.gitlab.aakumykov.sociocat.models.User;

public class UserAuthorizedEvent {

    private User user;

    public UserAuthorizedEvent(User user) {
        this.user = user;
    }

    public User getUser() {
        return this.user;
    }
}
