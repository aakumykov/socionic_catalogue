package ru.aakumykov.me.sociocat.event_objects;

public class LoginRequestSuccess {

    private String requestedAction;

    public LoginRequestSuccess(String requestedAction) {
        this.requestedAction = requestedAction;
    }

    public String getRequestedAction() {
        return requestedAction;
    }
}
