package ru.aakumykov.me.sociocat.tag_edit;

import android.content.Intent;

import ru.aakumykov.me.sociocat.base_view.iBaseView;

public interface iTagEdit_View extends iBaseView {

    Intent getIntent();

    String getTagName();
    void setTagName(String name);

    void confirmCancel();

    void showTagError(int messageId);
    void showTagError(String msg);


}
