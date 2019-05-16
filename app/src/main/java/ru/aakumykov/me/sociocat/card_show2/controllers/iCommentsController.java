package ru.aakumykov.me.sociocat.card_show2.controllers;

import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.models.Comment;

public interface iCommentsController extends iController {

    void loadComments(String parentCardId, @Nullable String start, int count);
    void editComment(Comment comment);
    void startReplyingComment(int position);
}
