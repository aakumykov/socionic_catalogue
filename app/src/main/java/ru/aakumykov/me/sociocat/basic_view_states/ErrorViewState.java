package ru.aakumykov.me.sociocat.basic_view_states;

public class ErrorViewState implements iBasicViewState {

    private final int mMessageId;
    private final String mDebugMessage;

    public ErrorViewState(int messageId, String debugMessage) {
        mMessageId = messageId;
        mDebugMessage = debugMessage;
    }

    public int getMessageId() {
        return mMessageId;
    }

    public String getDebugMessage() {
        return mDebugMessage;
    }
}
