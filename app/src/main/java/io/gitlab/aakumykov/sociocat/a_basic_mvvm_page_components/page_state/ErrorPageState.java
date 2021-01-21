package io.gitlab.aakumykov.sociocat.a_basic_mvvm_page_components.page_state;

import androidx.annotation.NonNull;

import io.gitlab.aakumykov.sociocat.a_basic_mvvm_page_components.interfaces.iMessageHolder;

public class ErrorPageState extends BasicPageState{

    private String mTag;
    private int mUserMessageId;
    private String mDebugMessage;


    public ErrorPageState(@NonNull String tag, int userMessageId, String debugMessage) {
        mTag = tag;
        mUserMessageId = userMessageId;
        mDebugMessage = debugMessage;
    }

    public ErrorPageState(@NonNull String tag, int userMessageId, @NonNull iMessageHolder messageHolder) {
        mTag = tag;
        mUserMessageId = userMessageId;
    }

    public String getTag() {
        return mTag;
    }

    public int getUserMessageId() {
        return 0;
    }

    public String getDebugMessage() {
        return null;
    }
}
