package ru.aakumykov.me.sociocat.card_show2;

import ru.aakumykov.me.complexrecyclerview.card_show2.models.Comment;

public interface iCommentsController extends iController {

    void loadComments(int start, int count);

    void editComment(Comment comment);
}
