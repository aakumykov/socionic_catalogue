package ru.aakumykov.me.sociocat.card_show2;


import ru.aakumykov.me.sociocat.models.Comment;

public interface iCommentsController extends iController {

    void loadComments(String start, int count);

    void editComment(Comment comment);
}
