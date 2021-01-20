package io.gitlab.aakumykov.sociocat.tag_edit.other;

import androidx.annotation.NonNull;

import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.interfaces.iViewState;
import io.gitlab.aakumykov.sociocat.models.Tag;

public class TagEditViewState implements iViewState {

    private final Tag mTag;

    public TagEditViewState(@NonNull Tag tag) {
        mTag = tag;
    }

    public Tag getTag() {
        return mTag;
    }
}
