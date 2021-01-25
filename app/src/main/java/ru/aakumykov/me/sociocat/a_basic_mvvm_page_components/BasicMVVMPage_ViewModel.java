package ru.aakumykov.me.sociocat.a_basic_mvvm_page_components;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ViewModel;

import ru.aakumykov.me.sociocat.a_basic_mvvm_page_components.page_event.BasicPageEvent;
import ru.aakumykov.me.sociocat.a_basic_mvvm_page_components.page_state.BasicPageState;


public abstract class BasicMVVMPage_ViewModel
        extends ViewModel
        implements LifecycleObserver
{
    protected MutableLiveData<BasicPageState> mPageStateLiveData;
    protected MutableLiveData<BasicPageEvent> mPageEventLiveData;
    private BasicPageState mCurrentPageState;


    public BasicMVVMPage_ViewModel() {
        mPageStateLiveData = new MutableLiveData<>();
        mPageEventLiveData = new MutableLiveData<>();
    }

    public LiveData<BasicPageState> getPageStateLiveData() {
        return mPageStateLiveData;
    }

    public LiveData<BasicPageEvent> getPageEventLiveData() {
        return mPageEventLiveData;
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        if (null == mCurrentPageState)
            onColdStart();
    }

    protected abstract void onColdStart();

    protected void setPageState(BasicPageState pageState) {
        mCurrentPageState = pageState;
        mPageStateLiveData.setValue(mCurrentPageState);
    }

    protected void risePageEvent(BasicPageEvent pageEvent) {
        mPageEventLiveData.setValue(pageEvent);
    }
}
