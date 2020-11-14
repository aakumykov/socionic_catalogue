package ru.aakumykov.me.sociocat.tag_edit.view_model;

import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ViewModel;

import ru.aakumykov.me.sociocat.basic_view_states.iBasicViewState;
import ru.aakumykov.me.sociocat.models.Tag;
import ru.aakumykov.me.sociocat.tag_edit.iTagEdit_View;
import ru.aakumykov.me.sociocat.tag_edit.stubs.TagEdit_ViewStub;

public class TagEdit_ViewModel extends ViewModel implements LifecycleObserver {

    private iTagEdit_View mView;
    private Tag mCurrentTag;
    private iBasicViewState mCurrentViewState;


    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private void onStart() {
        if (null == mCurrentTag)
            onColdStart(mView.getIntent());
        else
            onConfigChanged();
    }

    public void bindView(iTagEdit_View view) {
        mView = view;
    }

    public void unbindView() {
        mView = new TagEdit_ViewStub();
    }


    private void onColdStart(@Nullable Object data) {

    }

    private void onConfigChanged() {

    }
}
