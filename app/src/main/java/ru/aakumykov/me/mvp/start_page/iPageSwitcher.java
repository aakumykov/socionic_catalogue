package ru.aakumykov.me.mvp.start_page;

import android.support.annotation.Nullable;

public interface iPageSwitcher {
    void showCardsList(@Nullable String tagFilter);
    void showTagsList();
}
