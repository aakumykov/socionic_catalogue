package ru.aakumykov.me.sociocat.b_comments_list.interfaces;

import androidx.annotation.NonNull;

import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.interfaces.iBasicList_Page;
import ru.aakumykov.me.sociocat.models.Tag;

public interface iCommentsList_View extends iBasicList_Page {

    void goShowCommentedCard(@NonNull Tag tag);
}
