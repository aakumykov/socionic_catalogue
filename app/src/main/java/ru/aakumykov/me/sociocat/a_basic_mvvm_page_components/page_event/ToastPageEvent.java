package ru.aakumykov.me.sociocat.a_basic_mvvm_page_components.page_event;

import android.content.Context;

import androidx.annotation.NonNull;

public class ToastPageEvent extends BasicPageEvent {

    private int mMessageId;
    private String mMessageString;

    public ToastPageEvent(int messageId) {
        mMessageId = messageId;
    }

    public ToastPageEvent(String messageString) {
        mMessageString = messageString;
    }

    public String getMessage(@NonNull Context context) {
        if (null != mMessageString)
            return mMessageString;
        else
            return context.getString(mMessageId);
    }
}
