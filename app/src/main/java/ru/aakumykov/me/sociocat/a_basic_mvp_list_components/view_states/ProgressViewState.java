package ru.aakumykov.me.sociocat.a_basic_mvp_list_components.view_states;


import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.interfaces.iBasicViewState;

public class ProgressViewState implements iBasicViewState {

    private final int messageId;
    private final String messageString;

    public ProgressViewState(int messageId) {
        this.messageId = messageId;
        this.messageString = null;
    }

    public ProgressViewState(String messageString) {
        this.messageId = -1;
        this.messageString = messageString;
    }

    public String getStringMessage() {
        return messageString;
    }

    public int getMessageId() {
        return messageId;
    }

    public boolean hasStringMessage() {
        return null != messageString;
    }
}
