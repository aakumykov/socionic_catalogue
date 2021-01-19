package io.gitlab.aakumykov.sociocat.b_comments_list.interfaces;

import androidx.annotation.NonNull;

import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.interfaces.iBasicList_Page;
import io.gitlab.aakumykov.sociocat.models.Comment;

public interface iCommentsList_View extends iBasicList_Page {
    void goShowCommentUnderCard(@NonNull Comment comment);
    void goShowCommentedCard(@NonNull Comment comment);
    void goShowUserProfile(@NonNull String userId);
}
