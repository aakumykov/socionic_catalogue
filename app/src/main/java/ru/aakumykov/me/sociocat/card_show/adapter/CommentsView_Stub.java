package ru.aakumykov.me.sociocat.card_show.adapter;

import androidx.annotation.Nullable;

import java.util.List;

import ru.aakumykov.me.sociocat.models.Comment;

public class CommentsView_Stub implements iCommentsView {
    @Override public void showCommentsThrobber() {

    }

    @Override public void hideCommentsThrobber() {

    }

    @Override public void showCommentsError(int errorMsgId, String consoleErrorMsg) {

    }

    @Override public void hideCommentsError() {

    }

    @Override public void setList(List<Comment> itemsList) {

    }

    @Override public void appendList(List<Comment> list, int insertToIndex) {

    }

    @Override public void appendComment(Comment comment, @Nullable AttachCommentCallbacks callbacks) {

    }

    @Override public Comment getComment(String commentKey) {
        return null;
    }

    @Override public void scrollToComment(String commentKey) {

    }
}
