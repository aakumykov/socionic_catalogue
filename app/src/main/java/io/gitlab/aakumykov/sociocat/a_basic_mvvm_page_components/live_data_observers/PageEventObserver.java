package io.gitlab.aakumykov.sociocat.a_basic_mvvm_page_components.live_data_observers;

import androidx.lifecycle.Observer;

import io.gitlab.aakumykov.sociocat.a_basic_mvvm_page_components.page_event.BasicPageEvent;

public class PageEventObserver implements Observer<BasicPageEvent> {

    private final PageEventChangeCallback mChangeCallback;

    public PageEventObserver(PageEventChangeCallback changeCallback) {
        mChangeCallback = changeCallback;
    }

    @Override
    public void onChanged(BasicPageEvent pageEvent) {
        if (!pageEvent.isConsumed())
            mChangeCallback.onPageEventOccurred(pageEvent);
    }


    public interface PageEventChangeCallback {
        void onPageEventOccurred(BasicPageEvent pageEvent);
    }
}
