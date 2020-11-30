package ru.aakumykov.me.sociocat.tag_edit.other;

import androidx.annotation.NonNull;

import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.interfaces.iBasicViewState;
import ru.aakumykov.me.sociocat.models.Tag;

public class TagEditViewState implements iBasicViewState {

    private final Tag mTag;

    public TagEditViewState(@NonNull Tag tag) {
        mTag = tag;
    }

    public Tag getTag() {
        return mTag;
    }
}
