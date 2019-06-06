package ru.aakumykov.me.sociocat.card_show.adapter;

import androidx.annotation.Nullable;

import java.util.List;

import ru.aakumykov.me.sociocat.models.Comment;

public class CommentsAdapter_Stub implements iCommentsAdapter {
    @Override public void showCommentsThrobber(int position) {

    }

    @Override
    public void hideCommentsThrobber(int position) {

    }

    @Override public void showCommentsError(int errorMsgId, String consoleErrorMsg) {

    }

    @Override public void hideCommentsError() {

    }

    @Override public void setList(List<Comment> itemsList) {

    }

    @Override public void addList(List<Comment> list, int position, Comment alreadyVisibleTailComment) {

    }

    @Override public void attachComment(Comment comment, @Nullable AttachCommentCallbacks callbacks) {

    }

    @Override public Comment getComment(String commentKey) {
        return null;
    }

    @Override public void scrollToComment(String commentKey) {

    }
}
