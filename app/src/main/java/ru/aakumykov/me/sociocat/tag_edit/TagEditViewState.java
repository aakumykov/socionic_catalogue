package ru.aakumykov.me.sociocat.tag_edit;

import androidx.annotation.NonNull;

import ru.aakumykov.me.sociocat.basic_view_states.iBasicViewState;
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
