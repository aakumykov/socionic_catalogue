package ru.aakumykov.me.sociocat.card_show.adapter;

import java.util.List;

import ru.aakumykov.me.sociocat.models.Comment;

public interface iListAdapter_Comments {

    void showCommentsThrobber();
    void hideCommentsThrobber();

    void showCommentsError(int errorMsgId, String consoleErrorMsg);
    void hideCommentsError();

    void setList(List<Comment> itemsList);
    void addList(List<Comment> list);

    void scrollToComment(String commentKey);
}
