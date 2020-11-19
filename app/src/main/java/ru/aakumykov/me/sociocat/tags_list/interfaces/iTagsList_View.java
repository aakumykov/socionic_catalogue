package ru.aakumykov.me.sociocat.tags_list.interfaces;

import androidx.annotation.NonNull;

import ru.aakumykov.me.sociocat.b_basic_mvp_components2.interfaces.iBasicList_Page;
import ru.aakumykov.me.sociocat.models.Tag;

public interface iTagsList_View extends iBasicList_Page {

    void goShowCardsWithTag(@NonNull Tag tag);
    void goEditTag(@NonNull Tag tag);
}
