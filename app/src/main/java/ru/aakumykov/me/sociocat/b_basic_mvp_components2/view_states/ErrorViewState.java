package ru.aakumykov.me.sociocat.b_basic_mvp_components2.view_states;


import ru.aakumykov.me.sociocat.b_basic_mvp_components2.interfaces.iBasicViewState;

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
