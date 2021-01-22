package io.gitlab.aakumykov.sociocat.d_login2.page_events;

import android.content.Context;

import androidx.annotation.NonNull;

import io.gitlab.aakumykov.sociocat.a_basic_mvvm_page_components.page_event.BasicPageEvent;

public class LoginSuccessPageEvent extends BasicPageEvent {

    private String mMessageString;
    private int mMessageId;

    public LoginSuccessPageEvent(@NonNull String message) {
        mMessageString = message;
    }

    public LoginSuccessPageEvent(int messageId) {
        mMessageId = messageId;
    }

    public String getMessage(@NonNull Context context) {
        if (null != mMessageString)
            return mMessageString;
        else
            return context.getString(mMessageId);
    }
}
