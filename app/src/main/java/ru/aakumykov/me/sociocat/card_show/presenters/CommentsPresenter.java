package ru.aakumykov.me.sociocat.card_show.presenters;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import java.util.List;

import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_show.CardShow_View;
import ru.aakumykov.me.sociocat.card_show.adapter.CommentsView_Stub;
import ru.aakumykov.me.sociocat.card_show.adapter.iCommentsView;
import ru.aakumykov.me.sociocat.card_show.iPageView;
import ru.aakumykov.me.sociocat.card_show.list_items.ListItem;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.models.Comment;
import ru.aakumykov.me.sociocat.singletons.AuthSingleton;
import ru.aakumykov.me.sociocat.singletons.CommentsSingleton;
import ru.aakumykov.me.sociocat.singletons.iCommentsSingleton;

public class CommentsPresenter implements iCommentsPresenter{

    private iCommentsView commentsView;
    private iPageView pageView;
    private iCommentsSingleton commentsSingleton = CommentsSingleton.getInstance();


    @Override
    public void bindPageView(iPageView pageView) {
        this.pageView = pageView;
    }

    @Override
    public void unbindPageView() {
        this.pageView = null;
    }

    @Override
    public void bindCommentsView(iCommentsView commentsView) {
        this.commentsView = commentsView;
    }

    @Override
    public void unbindCommentsView() {
        this.commentsView = new CommentsView_Stub();
    }

    @Override
    public void onWorkBegins(@Nullable String cardKey, @Nullable String commentKey) {

        commentsView.showCommentsThrobber();

        commentsSingleton.loadList(cardKey, new iCommentsSingleton.ListCallbacks() {
            @Override
            public void onCommentsLoadSuccess(List<Comment> list) {
                commentsView.hideCommentsThrobber();
                commentsView.setList(list);

                if (null != commentKey)
                    commentsView.scrollToComment(commentKey);
            }

            @Override
            public void onCommentsLoadError(String errorMessage) {
                commentsView.hideCommentsThrobber();
                commentsView.showCommentsError(R.string.COMMENTS_error_loading_comments, errorMessage);
            }
        });
    }

    @Override
    public void onReplyToCommentClicked(String commentKey) {
        if (AuthSingleton.isLoggedIn()) {
            Comment comment = commentsView.getComment(commentKey);
            commentsView.showCommentForm(comment);
        }
        else {
            Bundle arguments = new Bundle();
                   arguments.putString(Constants.COMMENT_KEY, commentKey);

            pageView.requestLogin(Constants.CODE_REPLY_TO_COMMENT, arguments);
        }
    }

    // TODO: почему это делает CommentsPresenter?
    @Override
    public void onSendCommentClicked(String commentText, ListItem repliedItem, ru.aakumykov.me.sociocat.utils.comment_form.iCommentForm commentForm) {

        commentText = commentText.trim();

        if (TextUtils.isEmpty(commentText))
            return;


        Comment newComment = new Comment();
        newComment.setText(commentText);

        switch (repliedItem.getItemType()) {
            case CARD_ITEM:
                newComment.setCardId(((Card)repliedItem).getKey());
                break;
            case COMMENT_ITEM:
                Comment repliedComment = (Comment)repliedItem;
                newComment.setCardId(repliedComment.getCardId());
                newComment.setParentId(repliedComment.getKey());
                newComment.setParentText(repliedComment.getText());
                break;
            default:
                break;
        }

        commentForm.disable();

        commentsSingleton.createComment(newComment, new iCommentsSingleton.CreateCallbacks() {
            @Override
            public void onCommentSaveSuccess(Comment comment) {
                commentForm.hide();

                commentsView.attachComment(comment, new iCommentsView.AttachCommentCallbacks() {
                    @Override public void onCommentAttached(Comment comment) {
                        commentsView.scrollToComment(comment.getKey());
                    }
                });
            }

            @Override
            public void onCommentSaveError(String errorMsg) {
                commentForm.enable();
                commentForm.showError(R.string.COMMENT_error_adding_comment, errorMsg);
            }
        });
    }
}
