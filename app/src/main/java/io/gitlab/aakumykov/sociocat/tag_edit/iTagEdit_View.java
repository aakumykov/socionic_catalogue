package io.gitlab.aakumykov.sociocat.tag_edit;

import android.content.Intent;

import androidx.annotation.NonNull;

import io.gitlab.aakumykov.sociocat.models.Tag;
import io.gitlab.aakumykov.sociocat.z_base_view.iBaseView;

public interface iTagEdit_View extends iBaseView {

    Intent getIntent();

    String getTagName();
    void setTagName(String name);

    void confirmCancel();

    void showTagError(int messageId);
    void showTagError(String msg);

    void finishWithSuccess(@NonNull Tag oldTag, @NonNull Tag newTag);
}
