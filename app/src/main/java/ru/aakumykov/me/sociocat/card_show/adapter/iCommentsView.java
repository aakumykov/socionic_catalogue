package ru.aakumykov.me.sociocat.card_show.adapter;

import androidx.annotation.Nullable;

import java.util.List;

import ru.aakumykov.me.sociocat.models.Comment;

public interface iCommentsView {

    interface AttachCommentCallbacks {
        void onCommentAttached(Comment comment);
    }

    void showCommentsThrobber(int position);
    void hideCommentsThrobber(int position);

    void showCommentsError(int errorMsgId, String consoleErrorMsg);
    void hideCommentsError();

    void showDeleteDialog(Comment comment);

    void setList(List<Comment> itemsList);
    void addList(List<Comment> list, int position, @Nullable Comment alreadyVisibleTailComment);

    void attachComment(Comment comment, @Nullable AttachCommentCallbacks callbacks);
    void updateComment(Comment oldComment, Comment newComment);

    Comment getComment(String commentKey);

    void scrollToComment(String commentKey);
}
