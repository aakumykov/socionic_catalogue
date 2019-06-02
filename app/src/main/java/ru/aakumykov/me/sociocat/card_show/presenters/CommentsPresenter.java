package ru.aakumykov.me.sociocat.card_show.presenters;

import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import org.w3c.dom.Text;

import java.util.List;

import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_show.adapter.CommentsView_Stub;
import ru.aakumykov.me.sociocat.card_show.adapter.iCommentsView;
import ru.aakumykov.me.sociocat.card_show.iPageView;
import ru.aakumykov.me.sociocat.card_show.list_items.ListItem;
import ru.aakumykov.me.sociocat.interfaces.iMyDialogs;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.models.Comment;
import ru.aakumykov.me.sociocat.models.User;
import ru.aakumykov.me.sociocat.singletons.AuthSingleton;
import ru.aakumykov.me.sociocat.singletons.CommentsSingleton;
import ru.aakumykov.me.sociocat.singletons.UsersSingleton;
import ru.aakumykov.me.sociocat.singletons.iCommentsSingleton;
import ru.aakumykov.me.sociocat.singletons.iUsersSingleton;
import ru.aakumykov.me.sociocat.utils.MyDialogs;
import ru.aakumykov.me.sociocat.utils.comment_form.iCommentForm;

public class CommentsPresenter implements iCommentsPresenter {

    private enum LoadMode {
        MODE_APPEND, MODE_REPLACE
    }

    private iCommentsView commentsView;
    private iPageView pageView;
    private iCommentsSingleton commentsSingleton = CommentsSingleton.getInstance();
    private iUsersSingleton usersSingleton = UsersSingleton.getInstance();

    private ListItem repliedItem;
    private Comment editedComment;


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
        loadComments(LoadMode.MODE_REPLACE, cardKey, null, scrollToCommentKey);
    }

    @Override
    public void onLoadMoreClicked(String cardKey, String lastVisibleCommentKey) {
        loadComments(LoadMode.MODE_APPEND, cardKey, lastVisibleCommentKey, null);
    }

    @Override
    public void onReplyToCommentClicked(String commentKey) {
        if (AuthSingleton.isLoggedIn()) {
            Comment comment = commentsView.getComment(commentKey);
            this.repliedItem = comment;
            pageView.showCommentForm(comment);
        } else {
            Bundle transitAgruments = new Bundle();
            transitAgruments.putString(Constants.COMMENT_KEY, commentKey);
            pageView.requestLogin(Constants.CODE_REPLY_TO_COMMENT, transitAgruments);
        }
    }

    @Override
    public void onSendCommentClicked(iCommentForm commentForm) {

        String commentText = commentForm.getText().trim();

        if (TextUtils.isEmpty(commentText)) {
            commentForm.showError(R.string.cannot_be_empty, null);
            return;
        }

        User user = usersSingleton.getCurrentUser();

        Comment newComment = new Comment();
                newComment.setText(commentText);
                newComment.setUserId(user.getKey());
                newComment.setUserName(user.getName());
                newComment.setUserAvatar(user.getAvatarURL());

        switch (this.repliedItem.getItemType()) {
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
                commentForm.clear();
                pageView.hideCommentForm();

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

    @Override
    public void onEditCommentClicked(Comment comment) {
        if (!AuthSingleton.isLoggedIn()) {
            pageView.showToast("Необходимо авторизоваться (╯°□°)╯");
            return;
        }

        /*MyDialogs.commentEditDialog(pageView.getActivity(), comment.getText(), new iMyDialogs.StringInputCallback() {
            @Override
            public String onPrepareText() {
                return null;
            }

            @Override
            public String onYesClicked(String text) {
                onCommentEditFinished(comment, text);
                return null;
            }

            @Override
            public void onSuccess(String inputtedString) {

            }
        });*/

        MyDialogs.stringInputDialog(
                pageView.getActivity(),
                R.string.CARD_SHOW_comment_edition,
                "Сообщение: чо?",
                "Подсказка",
                new iMyDialogs.StringInputCallback() {
                    @Override
                    public String onPrepareText() {
                        return null;
                    }

                    @Override
                    public String onYesClicked(String text) {
                        return null;
                    }

                    @Override
                    public void onSuccess(String inputtedString) {

                    }
                }
        );
    }

    @Override
    public void onCommentEditFinished(Comment originalComment, String newText) {

    }


    // Внутренние методы
    private void loadComments(LoadMode loadMode, String cardKey, @Nullable String lastCommentKey, @Nullable String scrollToCommentKey) {

        commentsView.showCommentsThrobber();

        commentsSingleton.loadList(cardKey, lastCommentKey, new iCommentsSingleton.ListCallbacks() {
            @Override
            public void onCommentsLoadSuccess(List<Comment> list) {
                commentsView.hideCommentsThrobber();

                if (LoadMode.MODE_REPLACE.equals(loadMode))
                    commentsView.setList(list);
                else
                    commentsView.appendList(list);

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
}
