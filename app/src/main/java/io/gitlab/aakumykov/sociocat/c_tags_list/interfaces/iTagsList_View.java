package io.gitlab.aakumykov.sociocat.c_tags_list.interfaces;

import androidx.annotation.NonNull;

import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.interfaces.iBasicList_Page;
import io.gitlab.aakumykov.sociocat.models.Tag;

public interface iTagsList_View extends iBasicList_Page {

    void goShowCardsWithTag(@NonNull Tag tag);
    void goEditTag(@NonNull Tag tag);
}
