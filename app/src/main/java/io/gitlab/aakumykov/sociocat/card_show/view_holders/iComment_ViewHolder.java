package io.gitlab.aakumykov.sociocat.card_show.view_holders;

import androidx.annotation.Nullable;

import io.gitlab.aakumykov.sociocat.singletons.iCommentsSingleton;

public interface iComment_ViewHolder {
    void fadeBackground();
    void unfadeBackground();

    void disableRatingControls();
    void enablRatingControls();

    void setRating(int value);

    void colorizeRatingWidget(@Nullable iCommentsSingleton.CommentRatingAction commentRatingAction);
}
