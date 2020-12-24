package ru.aakumykov.me.sociocat.a_basic_mvp_list_components.view_states;


import android.content.Context;

import androidx.annotation.NonNull;

import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.interfaces.iViewState;

public class ErrorViewState implements iViewState {

    private final int mMessageId;
    private final String mDebugMessage;
    private final String mMessageText;

    
    public ErrorViewState(int messageId, String debugMessage) {
        mMessageId = messageId;
        mMessageText = null;
        mDebugMessage = debugMessage;
    }

    public ErrorViewState(@NonNull String messageText, String debugMessage) {
        mMessageId = -1;
        mMessageText = messageText;
        mDebugMessage = debugMessage;
    }


    public String getMessage(@NonNull Context context) {
        if (-1 != mMessageId)
            return context.getResources().getString(mMessageId);
        else
            return mMessageText;
    }

    public String getDebugMessage() {
        return mDebugMessage;
    }
}
