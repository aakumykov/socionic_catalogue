package ru.aakumykov.me.sociocat.tag_edit.view_model;

import android.content.Intent;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ViewModel;

import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.basic_view_states.ErrorViewState;
import ru.aakumykov.me.sociocat.basic_view_states.NeutralViewState;
import ru.aakumykov.me.sociocat.basic_view_states.ProgressViewState;
import ru.aakumykov.me.sociocat.basic_view_states.iBasicViewState;
import ru.aakumykov.me.sociocat.models.Tag;
import ru.aakumykov.me.sociocat.singletons.ComplexSingleton;
import ru.aakumykov.me.sociocat.singletons.TagsSingleton;
import ru.aakumykov.me.sociocat.singletons.UsersSingleton;
import ru.aakumykov.me.sociocat.singletons.iTagsSingleton;
import ru.aakumykov.me.sociocat.tag_edit.TagEditViewState;
import ru.aakumykov.me.sociocat.tag_edit.iTagEdit_View;
import ru.aakumykov.me.sociocat.tag_edit.stubs.TagEdit_ViewStub;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class TagEdit_PageController extends ViewModel implements LifecycleObserver {

    private iTagEdit_View mPageView;
    private Tag mCurrentTag;
    private iBasicViewState mCurrentViewState;
    private final TagsSingleton mTagsSingleton = TagsSingleton.getInstance();
    private final UsersSingleton mUsersSingleton = UsersSingleton.getInstance();
    private final ComplexSingleton mComplexSingleton = ComplexSingleton.getInstance();


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
        // Проверка права редактировать метку
        if (!mUsersSingleton.currentUserIsAdmin()) {
            mPageView.showToast(R.string.TAG_EDIT_you_cannot_edit_tag);
            return;
        }

        // Проверка изменения метки
        if (!tagIsEdited()) {
            mPageView.showToast(R.string.TAG_EDIT_tag_is_not_changed);
            return;
        }

        // Удаление концевых пробелов
        String newTagName = mPageView.getTagName().trim();
        mPageView.setTagName(newTagName);

        // Проверка на "пустоту"
        if (TextUtils.isEmpty(newTagName)) {
            mPageView.showTagError(R.string.cannot_be_empty);
            return;
        }

        // Проверка длины
        int len = newTagName.length();
        if (len > Constants.TAG_NAME_MAX_LENGTH) {
            String msg = MyUtils.getString(
                    mPageView.getAppContext(),
                    R.string.TAG_EDIT_name_is_too_long,
                    Constants.TAG_NAME_MAX_LENGTH,
                    len
                );
            mPageView.showTagError(msg);
            return;
        }

        // Сохранение...
        setViewState(new ProgressViewState(R.string.TAG_EDIT_saving_tag));

        Tag newTag = Tag.getCloneOf(mCurrentTag);
        newTag.setName(mPageView.getTagName());

        // Проверка на дубликат
        // TODO: сделать запрет на уровне БД!
        mTagsSingleton.getTag(newTag.getKey(), new iTagsSingleton.TagCallbacks() {
            @Override
            public void onTagSuccess(Tag tag) {
                setViewState(new ErrorViewState(
                        R.string.TAG_EDIT_duplicate_found,
                        "Tag «"+newTag.getName()+"» already exists"));
            }

            @Override
            public void onTagFail(String errorMsg) {
                setViewState(new NeutralViewState());
                mPageView.showToast("Можно сохранять");
            }
        });
    }

    private void updateTag(@NonNull Tag newTag, @NonNull Tag oldTag) {

        mComplexSingleton.updateTag(mCurrentTag, newTag, new ComplexSingleton.iComplexSingleton_TagSaveCallbacks() {
            @Override
            public void onTagSaveSuccess(@NonNull Tag tag) {

            }

            @Override
            public void onTagSaveError(@NonNull String errorMsg) {

            }
        });
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
