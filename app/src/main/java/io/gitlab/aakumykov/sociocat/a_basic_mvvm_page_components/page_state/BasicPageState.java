package io.gitlab.aakumykov.sociocat.a_basic_mvvm_page_components.page_state;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.gitlab.aakumykov.sociocat.a_basic_mvvm_page_components.interfaces.iMessageHolder;

public abstract class BasicPageState implements iMessageHolder {

    @Nullable
    private String mMsg;

    public BasicPageState() {

    }

    public BasicPageState(@NonNull String msg) {
        mMsg = msg;
    }

    @Override
    public String getMessage() {
        return mMsg;
    }
}
