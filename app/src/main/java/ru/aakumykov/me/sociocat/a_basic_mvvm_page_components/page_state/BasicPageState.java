package ru.aakumykov.me.sociocat.a_basic_mvvm_page_components.page_state;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.a_basic_mvvm_page_components.interfaces.iMessageHolder;

public abstract class BasicPageState implements iMessageHolder {

    @Nullable
    private String mMessageString;
    private int mMessageId;

    public BasicPageState() {

    }

    public BasicPageState(@NonNull String messageString) {
        mMessageString = messageString;
    }

    public BasicPageState(int messageId) {
        mMessageId = messageId;
    }


    @Override
    public String getMessage(@NonNull Context context) {
        if (null != mMessageString)
            return mMessageString;
        else
            return context.getString(mMessageId);
    }
}
