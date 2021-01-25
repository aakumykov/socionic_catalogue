package ru.aakumykov.me.sociocat.a_basic_mvvm_page_components.page_state;

import androidx.annotation.Nullable;

public class PageState {

    private final iPageStateName mPageStateName;
    private final Object mPageStateData;


    public PageState(iPageStateName pageStateName, @Nullable Object data) {
        mPageStateName = pageStateName;
        mPageStateData = data;
    }


    public iPageStateName getName() {
        return mPageStateName;
    }

    public Object getPageStateData() {
        return mPageStateData;
    }
}
