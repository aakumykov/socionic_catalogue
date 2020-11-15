package ru.aakumykov.me.sociocat.tag_edit;

import android.content.Intent;

import androidx.annotation.NonNull;

import ru.aakumykov.me.sociocat.base_view.iBaseView;
import ru.aakumykov.me.sociocat.models.Tag;

public interface iTagEdit_View extends iBaseView {

    Intent getIntent();

    String getTagName();
    void setTagName(String name);

    void confirmCancel();

    void showTagError(int messageId);
    void showTagError(String msg);

    void finishWithSuccess(@NonNull Tag tag);
}
