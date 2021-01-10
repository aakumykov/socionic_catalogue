package ru.aakumykov.me.sociocat.b_comments_list.interfaces;

import androidx.annotation.NonNull;

import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.interfaces.iBasicMVP_ItemClickListener;
import ru.aakumykov.me.sociocat.b_comments_list.view_holders.CommentViewHolder;

public interface iCommentsList_ItemClickListener extends iBasicMVP_ItemClickListener {
    void onCardTitleClicked(@NonNull CommentViewHolder commentViewHolder);
    void onCardAuthorClicked(@NonNull CommentViewHolder userId);
}
