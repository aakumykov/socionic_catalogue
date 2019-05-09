package ru.aakumykov.me.sociocat.card_show_2;

import java.util.List;

import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.models.Comment;

public interface iDataAdapter {

    interface AppendCommentCallbacks {
        void onCommentAppended();
    }

    void setCard(Card card);
    void appendComments(List<Comment> list);
    void appendComment(Comment comment, AppendCommentCallbacks callbacks);
    int findCommentPosition(Comment comment);

    void setComment(int position, Comment comment);
    void insertComment(int position, Comment comment);
    void removeComment(int position);
    Comment getLastComment();

    void clearList();

    void showLoadMoreItem();
    void hideLoadMoreItem();

    void showCommentsThrobber();
    void hideCommentsThrobber();

}
