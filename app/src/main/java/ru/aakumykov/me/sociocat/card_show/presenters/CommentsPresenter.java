package ru.aakumykov.me.sociocat.card_show.presenters;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import java.util.List;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_show.adapter.ListAdapter_Stub;
import ru.aakumykov.me.sociocat.card_show.adapter.iListAdapter_Comments;
import ru.aakumykov.me.sociocat.card_show.list_items.ListItem;
import ru.aakumykov.me.sociocat.card_show.view_holders.Comment_ViewHolder;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.models.Comment;
import ru.aakumykov.me.sociocat.singletons.CommentsSingleton;
import ru.aakumykov.me.sociocat.singletons.iCommentsSingleton;

public class CommentsPresenter implements iCommentsPresenter{

    private iListAdapter_Comments listAdapter;
    private iCommentsSingleton commentsSingleton = CommentsSingleton.getInstance();

    @Override
    public void bindListAdapter(iListAdapter_Comments listAdapter) {
        this.listAdapter = listAdapter;
    }

    @Override
    public void unbindListAdapter() {
        this.listAdapter = new ListAdapter_Stub();
    }

    @Override
    public void onWorkBegins(@Nullable String cardKey, @Nullable String commentKey) {

        listAdapter.showCommentsThrobber();

        commentsSingleton.loadList(cardKey, new iCommentsSingleton.ListCallbacks() {
            @Override
            public void onCommentsLoadSuccess(List<Comment> list) {
                listAdapter.hideCommentsThrobber();
                listAdapter.setList(list);

                if (null != commentKey)
                    listAdapter.scrollToComment(commentKey);
            }

            @Override
            public void onCommentsLoadError(String errorMessage) {
                listAdapter.hideCommentsThrobber();
                listAdapter.showCommentsError(R.string.COMMENTS_error_loading_comments, errorMessage);
            }
        });
    }

    @Override
    public void onReplyToCommentClicked(Comment_ViewHolder commentViewHolder, Comment comment) {
        //commentFormView.showCommentForm(comment.getText(), comment);
    }

    @Override
    public void onSendCommentClicked(String commentText, ListItem repliedItem, ru.aakumykov.me.sociocat.card_show.comment_form.iCommentForm commentForm) {

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
                listAdapter.addComment(comment, true);
            }

            @Override
            public void onCommentSaveError(String errorMsg) {
                commentForm.enable();
                // TODO: где и как показывать ошибку?
                //commentFormView.showErrorMsg(R.string.COMMENT_error_adding_comment, errorMsg);
            }
        });
    }
}
