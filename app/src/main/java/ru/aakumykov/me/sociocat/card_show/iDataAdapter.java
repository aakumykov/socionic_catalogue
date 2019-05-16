package ru.aakumykov.me.sociocat.card_show;


import java.util.List;

import ru.aakumykov.me.sociocat.card_show.controllers.iCardController;
import ru.aakumykov.me.sociocat.card_show.controllers.iCommentsController;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.models.Comment;

public interface iDataAdapter {

    interface AppendCommentCallbacks {
        void onCommentAppended();
    }

    void bindControllers(iCardController cardController, iCommentsController commentsController);
    void unbindControllers();

    void setCard(Card card);
    void appendComments(List<Comment> list);

    void appendComment(Comment comment, AppendCommentCallbacks callbacks);
    void insertComment(int position, Comment comment);
    void updateComment(int position, Comment comment);
    void removeComment(int position);

    int findCommentPosition(Comment comment);
    Comment getLastComment();

    void clearList();

    void hideLastServiceItem();

    void showLoadMoreItem();
    void hideLoadMoreItem();

    void showCommentsThrobber();
    void hideCommentsThrobber();

}
