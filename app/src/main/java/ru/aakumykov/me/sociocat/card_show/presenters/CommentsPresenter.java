package ru.aakumykov.me.sociocat.card_show.presenters;

import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import java.util.List;

import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_show.adapter.CommentsView_Stub;
import ru.aakumykov.me.sociocat.card_show.adapter.iCommentsView;
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

public class CommentsPresenter implements iCommentsPresenter {

    private enum LoadMode {
        MODE_APPEND, MODE_REPLACE
    }

    private iCommentsView commentsView;
    private iPageView pageView;
    private iCommentsSingleton commentsSingleton = CommentsSingleton.getInstance();
    private iUsersSingleton usersSingleton = UsersSingleton.getInstance();

    private iTextItem mRepliedItem;
    private Comment mEditedComment;
    private Comment endComment;


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
    public void onWorkBegins(String cardKey, @Nullable String commentKeyToScroll) {
        loadComments(LoadMode.MODE_REPLACE, cardKey, -1, null, null, commentKeyToScroll);
    }

    @Override
    public void onLoadMoreClicked(int itemIndex, Comment startAtComment) {
        int insertToIndex = itemIndex - 1;
        String cardKey = startAtComment.getCardId();
        String startKey = startAtComment.getKey();
        String endKey = endComment.getKey();
        loadComments(LoadMode.MODE_APPEND, cardKey, insertToIndex, startKey, endKey, null);
    }

    @Override
    public void onReplyClicked(iTextItem repliedItem) {
        mRepliedItem = repliedItem;

        if (AuthSingleton.isLoggedIn()) {
            pageView.showCommentForm(repliedItem);
        }
        else {
            Bundle transitAgruments = new Bundle();
            transitAgruments.putParcelable(Constants.REPLIED_ITEM, repliedItem);
            pageView.requestLogin(Constants.CODE_REPLY, transitAgruments);
        }
    }

    @Override
    public void onEditCommentClicked(Comment comment) {
        if (!AuthSingleton.isLoggedIn()) {
            pageView.showToast("Необходимо авторизоваться (╯°□°)╯");
            return;
        }

        mEditedComment = comment;

        pageView.showCommentForm(comment);
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
    private void loadComments(LoadMode loadMode,
                              String cardKey,
                              int insertToIndex,
                              @Nullable String startKey,
                              @Nullable String endKey,
                              @Nullable String scrollToCommentKey
    ) {

        commentsView.showCommentsThrobber();

        commentsSingleton.loadList(cardKey, startKey, endKey, new iCommentsSingleton.ListCallbacks() {
            @Override
            public void onCommentsLoadSuccess(List<Comment> list) {
                commentsView.hideCommentsThrobber();

                endComment = list.get(list.size()-1);

                if (LoadMode.MODE_REPLACE.equals(loadMode))
                    commentsView.setList(list);
                else
                    commentsView.appendList(list, insertToIndex);

                if (null != scrollToCommentKey)
                    commentsView.scrollToComment(scrollToCommentKey);
            }

            @Override
            public void onCommentsLoadError(String errorMessage) {
                commentsView.hideCommentsThrobber();
                commentsView.showCommentsError(R.string.COMMENTS_error_loading_comments, errorMessage);
            }
        });
    }

    private void createComment(String commentText, iCommentForm commentForm) {
        User user = usersSingleton.getCurrentUser();

        Comment newComment = new Comment();
                newComment.setText(commentText);
                newComment.setUserId(user.getKey());
                newComment.setUserName(user.getName());
                newComment.setUserAvatar(user.getAvatarURL());

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
                pageView.hideCommentForm();

                mEditedComment = null;
                mRepliedItem = null;

                appendComment(comment, true);
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
                pageView.hideCommentForm();

                mEditedComment = null;
                mRepliedItem = null;

                appendComment(comment, false);
            }

            @Override
            public void onCommentSaveError(String errorMsg) {
                commentForm.showError(R.string.CARD_SHOW_error_saving_comment, errorMsg);
                commentForm.enable();
            }
        });
    }

    private void appendComment(Comment comment, boolean scrollToIt) {
        this.endComment = comment;

        commentsView.appendComment(comment, new iCommentsView.AttachCommentCallbacks() {
            @Override public void onCommentAttached(Comment comment) {
                if (scrollToIt)
                    commentsView.scrollToComment(comment.getKey());
            }
        });
    }
}
