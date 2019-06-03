package ru.aakumykov.me.sociocat.card_show.adapter;

import androidx.annotation.Nullable;

import java.util.List;

import ru.aakumykov.me.sociocat.card_show.list_items.ListItem;
import ru.aakumykov.me.sociocat.models.Comment;

public interface iCommentsView {

    interface AttachCommentCallbacks {
        void onCommentAttached(Comment comment);
    }

    void showCommentsThrobber();
    void hideCommentsThrobber();

    void showCommentsError(int errorMsgId, String consoleErrorMsg);
    void hideCommentsError();

    void setList(List<Comment> itemsList);
    void appendList(List<Comment> list);

    void attachComment(Comment comment, @Nullable AttachCommentCallbacks callbacks);

    Comment getComment(String commentKey);

    void scrollToComment(String commentKey);
}
