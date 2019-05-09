package ru.aakumykov.me.sociocat.card_show_2;

import ru.aakumykov.me.sociocat.models.Comment;

public interface iCommentsController extends iController {

    void loadComments(int start, int count);

    void editComment(Comment comment);
}
