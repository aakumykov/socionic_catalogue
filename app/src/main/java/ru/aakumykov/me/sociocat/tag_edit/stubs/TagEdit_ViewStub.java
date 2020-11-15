package ru.aakumykov.me.sociocat.tag_edit.stubs;

import android.content.Intent;

import ru.aakumykov.me.sociocat.base_view.BaseView_Stub;
import ru.aakumykov.me.sociocat.tag_edit.iTagEdit_View;

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
}
