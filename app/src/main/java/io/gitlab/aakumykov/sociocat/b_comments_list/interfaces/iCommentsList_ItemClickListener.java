package io.gitlab.aakumykov.sociocat.b_comments_list.interfaces;

import androidx.annotation.NonNull;

import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.interfaces.iBasicMVP_ItemClickListener;
import io.gitlab.aakumykov.sociocat.b_comments_list.view_holders.CommentViewHolder;

public interface iCommentsList_ItemClickListener extends iBasicMVP_ItemClickListener {
    void onCardTitleClicked(@NonNull CommentViewHolder commentViewHolder);
    void onCardAuthorClicked(@NonNull CommentViewHolder userId);
}
