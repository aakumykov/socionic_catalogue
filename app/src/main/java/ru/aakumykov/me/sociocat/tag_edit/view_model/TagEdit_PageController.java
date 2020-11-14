package ru.aakumykov.me.sociocat.tag_edit.view_model;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ViewModel;

import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.basic_view_states.ErrorViewState;
import ru.aakumykov.me.sociocat.basic_view_states.ProgressViewState;
import ru.aakumykov.me.sociocat.basic_view_states.iBasicViewState;
import ru.aakumykov.me.sociocat.models.Tag;
import ru.aakumykov.me.sociocat.singletons.TagsSingleton;
import ru.aakumykov.me.sociocat.singletons.UsersSingleton;
import ru.aakumykov.me.sociocat.singletons.iTagsSingleton;
import ru.aakumykov.me.sociocat.tag_edit.TagEditViewState;
import ru.aakumykov.me.sociocat.tag_edit.iTagEdit_View;
import ru.aakumykov.me.sociocat.tag_edit.stubs.TagEdit_ViewStub;

public class TagEdit_PageController extends ViewModel implements LifecycleObserver {

    private iTagEdit_View mPageView;
    private Tag mCurrentTag;
    private iBasicViewState mCurrentViewState;
    private final TagsSingleton mTagsSingleton = TagsSingleton.getInstance();
    private final UsersSingleton mUsersSingleton = UsersSingleton.getInstance();


    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private void onStart() {
        if (null == mCurrentTag)
            onColdStart(mPageView.getIntent());
        else
            onConfigChanged();
    }

    public void bindView(iTagEdit_View view) {
        mPageView = view;
    }

    public void unbindView() {
        mPageView = new TagEdit_ViewStub();
    }


    private void onColdStart(@Nullable Intent intent) {
        if (null == intent) {
            setViewState(new ErrorViewState(R.string.data_error, "Intent is null"));
            return;
        }

        String tagName = intent.getStringExtra(Constants.TAG_NAME);
        if (null == tagName) {
            setViewState(new ErrorViewState(R.string.data_error, "Tag name is null"));
            return;
        }

        loadAndShowTag(tagName);
    }

    private void loadAndShowTag(@NonNull String tagName) {
        setViewState(new ProgressViewState(R.string.TAG_EDIT_loading_tag));

        mTagsSingleton.getTag(tagName, new iTagsSingleton.TagCallbacks() {
            @Override
            public void onTagSuccess(Tag tag) {
                mCurrentTag = tag;
                setViewState(new TagEditViewState(tag));
            }

            @Override
            public void onTagFail(String errorMsg) {
                setViewState(new ErrorViewState(R.string.TAG_EDIT_error_loading_tag, errorMsg));
            }
        });
    }

    private void onConfigChanged() {
        setViewState(mCurrentViewState);
    }

    private void setViewState(iBasicViewState viewState) {
        mCurrentViewState = viewState;
        mPageView.setViewState(viewState);
    }

    public boolean onBackPressed() {
        if (tagIsEdited()) {
            mPageView.confirmCancel();
            return true;
        }
        else
            return false;
    }

    public void onSaveClicked() {
        if (!mUsersSingleton.currentUserIsAdmin()) {
            mPageView.showToast(R.string.TAG_EDIT_you_cannot_edit_tag);
            return;
        }

        if (!tagIsEdited()) {
            mPageView.showToast(R.string.TAG_EDIT_tag_is_not_changed);
            return;
        }

        setViewState(new ProgressViewState(R.string.TAG_EDIT_saving_tag));

        mCurrentTag.setName(mPageView.getTagName());

        /*mTagsSingleton.saveTag(mCurrentTag, new iTagsSingleton.SaveCallbacks() {
            @Override
            public void onSaveSuccess(Tag tag) {

            }

            @Override
            public void onSaveFail(String errorMsg) {

            }
        });*/
    }

    public void onCancelClicked() {
        if (tagIsEdited())
            mPageView.confirmCancel();
        else
            onCancelConfirmed();
    }

    public void onCancelConfirmed() {
        mPageView.closePage();
    }


    private boolean tagIsEdited() {
        if (null == mCurrentTag)
            return false;

        return !mCurrentTag.getName().equals(mPageView.getTagName());
    }
}
