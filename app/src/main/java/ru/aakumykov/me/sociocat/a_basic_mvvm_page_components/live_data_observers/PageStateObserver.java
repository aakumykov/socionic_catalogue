package ru.aakumykov.me.sociocat.a_basic_mvvm_page_components.live_data_observers;

import androidx.lifecycle.Observer;

import ru.aakumykov.me.sociocat.a_basic_mvvm_page_components.page_state.BasicPageState;

public class PageStateObserver implements Observer<BasicPageState> {

    private final PageStateChangeCallback mPageStateChangeCallback;

    public PageStateObserver(PageStateChangeCallback pageStateChangeCallback) {
        mPageStateChangeCallback = pageStateChangeCallback;
    }

    @Override
    public void onChanged(BasicPageState pageState) {
        mPageStateChangeCallback.onPageStateChanged(pageState);
    }


    public interface PageStateChangeCallback {
        void onPageStateChanged(BasicPageState pageState);
    }
}
