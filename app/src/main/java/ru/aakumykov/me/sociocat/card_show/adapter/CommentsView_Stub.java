package ru.aakumykov.me.sociocat.card_show.adapter;

import androidx.annotation.Nullable;

import java.util.List;

import ru.aakumykov.me.sociocat.card_show.iCardShow;
import ru.aakumykov.me.sociocat.models.Comment;

public class CommentsView_Stub implements iCardShow.iCommentsView {
    @Override public void showCommentsThrobber(int position) {

    }

    @Override
    public void hideCommentsThrobber(int position) {

    }

    @Override public void showCommentsError(int errorMsgId, String consoleErrorMsg) {

    }

    @Override public void hideCommentsError() {

    }

    @Override
    public void showDeleteDialog(Comment comment) {

    }

    @Override public void setList(List<Comment> itemsList) {

    }

    @Override public void addList(List<Comment> list, int position, Comment alreadyVisibleTailComment) {

    }

    @Override public void attachComment(Comment comment, @Nullable AttachCommentCallbacks callbacks) {

    }

    @Override
    public void updateComment(Comment oldComment, Comment newComment) {

    }

    @Override
    public void removeComment(Comment comment) {

    }

    @Override public Comment getComment(String commentKey) {
        return null;
    }

    @Override public void scrollToComment(String commentKey) {

    }
}
