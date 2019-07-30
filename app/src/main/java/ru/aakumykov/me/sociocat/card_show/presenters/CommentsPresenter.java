package ru.aakumykov.me.sociocat.card_show.presenters;

import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import java.util.List;

import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_show.adapter.CommentsView_Stub;
import ru.aakumykov.me.sociocat.card_show.adapter.iCommentsView;
import ru.aakumykov.me.sociocat.card_show.iCardShow;
import ru.aakumykov.me.sociocat.card_show.iPageView;
import ru.aakumykov.me.sociocat.card_show.list_items.iTextItem;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.models.Comment;
import ru.aakumykov.me.sociocat.models.User;
import ru.aakumykov.me.sociocat.singletons.AuthSingleton;
import ru.aakumykov.me.sociocat.singletons.CommentsSingleton;
import ru.aakumykov.me.sociocat.singletons.UsersSingleton;
import ru.aakumykov.me.sociocat.singletons.iCommentsSingleton;
import ru.aakumykov.me.sociocat.singletons.iUsersSingleton;
import ru.aakumykov.me.sociocat.utils.comment_form.iCommentForm;

public class CommentsPresenter implements iCardShow.iCommentsPresenter {

    private enum LoadMode {
        MODE_APPEND, MODE_REPLACE
    }

    private iCommentsView commentsView;
    private iPageView pageView;
    private iCommentsSingleton commentsSingleton = CommentsSingleton.getInstance();
    private iUsersSingleton usersSingleton = UsersSingleton.getInstance();

    private iTextItem mRepliedItem;
    private Comment mEditedComment;
    private @Nullable Comment mEndComment;


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
    public void onWorkBegins(String cardKey, @Nullable String scrollToCommentKey) {
        loadComments(
                LoadMode.MODE_REPLACE,
                cardKey,
                null,
                null,
                -1,
                scrollToCommentKey
        );
    }

    @Override
    public void onLoadMoreClicked(int insertPosition, Comment beginningComment) {
        String cardKey = beginningComment.getCardId();

        loadComments(
                LoadMode.MODE_APPEND,
                cardKey,
                beginningComment,
                mEndComment,
                insertPosition,
                null
        );
    }

    @Override
    public void onReplyClicked(iTextItem repliedItem) {
        mRepliedItem = repliedItem;

        if (AuthSingleton.isLoggedIn()) {
            pageView.showCommentForm(repliedItem, false);
        }
        else {
            Bundle transitArguments = new Bundle();
            transitArguments.putParcelable(Constants.REPLIED_ITEM, repliedItem);
            pageView.requestLogin(Constants.CODE_POST_REPLY, transitArguments);
        }
    }

    @Override
    public void onEditCommentClicked(Comment comment) {
        if (!AuthSingleton.isLoggedIn()) {
            pageView.showToast("Необходимо авторизоваться (╯°-°)╯");
            return;
        }

        mEditedComment = comment;

        pageView.showCommentForm(comment, true);
    }

    @Override
    public void onDeleteCommentClicked(Comment comment) {
        if (!AuthSingleton.isLoggedIn()) {
            pageView.showToast("(╯°-°)╯ Что же ты творишь?");
            return;
        }

        commentsView.showDeleteDialog(comment);
    }

    @Override
    public void onDeleteConfirmed(Comment comment) {
        commentsSingleton.deleteComment(comment, new iCommentsSingleton.DeleteCallbacks() {
            @Override
            public void onDeleteSuccess(Comment comment) {
                commentsView.removeComment(comment);
            }

            @Override
            public void onDeleteError(String msg) {
//                commentsView.showCommentsError(R.string.COMMENTS_VIEW_error_deleting_comment, msg);
                pageView.showErrorMsg(R.string.COMMENTS_VIEW_error_deleting_comment, msg);
            }
        });
    }

    @Override
    public void onSendCommentClicked(iCommentForm commentForm) {

        String commentText = commentForm.getText().trim();

        if (TextUtils.isEmpty(commentText)) {
            commentForm.showError(R.string.cannot_be_empty, null);
            return;
        }

        if (null != mRepliedItem)
            createComment(commentText, commentForm);
        else if (null != mEditedComment)
            updateComment(commentText, commentForm);
    }


    // Внутренние методы
    private void loadComments(
            LoadMode loadMode,
            String cardKey,
            @Nullable Comment startComment,
            @Nullable Comment endComment,
            int insertPosition,
            @Nullable String scrollToCommentKey
    ) {
        String startCommentKey = (null != startComment) ? startComment.getKey() : null;
        String endCommentKey = (null != endComment) ? endComment.getKey() : null;

        commentsView.showCommentsThrobber(insertPosition);

        commentsSingleton.loadList(
                cardKey,
                startCommentKey,
                endCommentKey,
                new iCommentsSingleton.ListCallbacks() {
                    @Override
                    public void onCommentsLoadSuccess(List<Comment> list) {

                        commentsView.hideCommentsThrobber(insertPosition);

                        if (LoadMode.MODE_REPLACE.equals(loadMode))
                            commentsView.setList(list);
                        else
                            commentsView.addList(list, insertPosition, endComment);

                        if (null != scrollToCommentKey)
                            commentsView.scrollToComment(scrollToCommentKey);
                    }

                    @Override
                    public void onCommentsLoadError(String errorMessage) {
                        commentsView.hideCommentsThrobber(insertPosition);
                        commentsView.showCommentsError(R.string.COMMENTS_error_loading_comments, errorMessage);
                    }
                }
        );
    }

    private void createComment(String commentText, iCommentForm commentForm) {
        User user = usersSingleton.getCurrentUser();

        Comment newComment = new Comment();
                newComment.setText(commentText);
                newComment.setUserId(user.getKey());
                newComment.setUserName(user.getName());
                newComment.setUserAvatarURL(user.getAvatarURL());

        switch (mRepliedItem.getItemType()) {
            case CARD_ITEM:
                newComment.setCardId(((Card) mRepliedItem).getKey());
                break;
            case COMMENT_ITEM:
                Comment repliedComment = (Comment) mRepliedItem;
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
                commentForm.clear();
                pageView.hideCommentForm(false);

                mEditedComment = null;
                mRepliedItem = null;
                mEndComment = comment;

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

    private void updateComment(String text, iCommentForm commentForm) {

        Comment modifiedComment = mEditedComment;
                modifiedComment.setText(text);

        commentForm.disable();

        commentsSingleton.updateComment(modifiedComment, new iCommentsSingleton.CreateCallbacks() {
            @Override
            public void onCommentSaveSuccess(Comment comment) {
                commentForm.clear();
                pageView.hideCommentForm(false);

                commentsView.updateComment(mEditedComment, comment);

                mEditedComment = null;
                mRepliedItem = null;
            }

            @Override
            public void onCommentSaveError(String errorMsg) {
                commentForm.showError(R.string.CARD_SHOW_error_saving_comment, errorMsg);
                commentForm.enable();
            }
        });
    }
}
