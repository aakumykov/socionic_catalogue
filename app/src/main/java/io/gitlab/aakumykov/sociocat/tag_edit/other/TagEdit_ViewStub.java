package io.gitlab.aakumykov.sociocat.tag_edit.other;

import android.content.Intent;

import androidx.annotation.NonNull;

import io.gitlab.aakumykov.sociocat.models.Tag;
import io.gitlab.aakumykov.sociocat.tag_edit.iTagEdit_View;
import io.gitlab.aakumykov.sociocat.z_base_view.BaseView_Stub;

public class TagEdit_ViewStub extends BaseView_Stub implements iTagEdit_View {

    @Override
    public Intent getIntent() {
        return null;
    }

    @Override
    public String getTagName() {
        return null;
    }

    @Override
    public void setTagName(String name) {

    }

    @Override
    public void confirmCancel() {

    }

    @Override
    public void showTagError(int messageId) {

    }

    @Override
    public void showTagError(String msg) {

    }

    @Override
    public void finishWithSuccess(@NonNull Tag oldTag, @NonNull Tag newTag) {

    }
}
