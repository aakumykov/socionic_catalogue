package io.gitlab.aakumykov.sociocat.a_basic_mvvm_page_components.page_event;

public abstract class BasicPageEvent {

    private boolean mIsConsumed = false;

    public void consume() {
        mIsConsumed = true;
    }

    public boolean isConsumed() {
        return mIsConsumed;
    }
}
