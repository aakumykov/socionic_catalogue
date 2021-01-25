package ru.aakumykov.me.sociocat.b_tags_list.interfaces;

import androidx.annotation.NonNull;

import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.interfaces.iBasicList_Page;
import ru.aakumykov.me.sociocat.models.Tag;

public interface iTagsList_View extends iBasicList_Page {

    void goShowCardsWithTag(@NonNull Tag tag);
    void goEditTag(@NonNull Tag tag);
}
