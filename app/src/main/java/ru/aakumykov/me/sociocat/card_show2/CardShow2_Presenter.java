package ru.aakumykov.me.sociocat.card_show2;

import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import java.util.List;

import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_show2.list_items.iList_Item;
import ru.aakumykov.me.sociocat.card_show2.stubs.CardShow2_ViewStub;
import ru.aakumykov.me.sociocat.card_show2.stubs.DataAdapter_Stub;
import ru.aakumykov.me.sociocat.interfaces.iMyDialogs;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.models.Comment;
import ru.aakumykov.me.sociocat.models.User;
import ru.aakumykov.me.sociocat.models.iCommentable;
import ru.aakumykov.me.sociocat.singletons.AuthSingleton;
import ru.aakumykov.me.sociocat.singletons.CardsSingleton;
import ru.aakumykov.me.sociocat.singletons.CommentsSingleton;
import ru.aakumykov.me.sociocat.singletons.UsersSingleton;
import ru.aakumykov.me.sociocat.singletons.iCardsSingleton;
import ru.aakumykov.me.sociocat.singletons.iCommentsSingleton;
import ru.aakumykov.me.sociocat.utils.MyDialogs;

public class CardShow2_Presenter implements iCardShow2.iPresenter {

    private final static String TAG = "CardShow2_Presenter";
    private AuthSingleton authSingleton = AuthSingleton.getInstance();
    private UsersSingleton usersSingleton = UsersSingleton.getInstance();
    private CardsSingleton cardsSingleton = CardsSingleton.getInstance();
    private CommentsSingleton commentsSingleton = CommentsSingleton.getInstance();
    private iCardShow2.iPageView pageView = null;
    private iCardShow2.iDataAdapter dataAdapter = null;

    private Card currentCard = null;
    private iList_Item currentListItem = null;
    private iCommentable repliedItem = null;
    private Comment editedComment = null;


    @Override
    public void bindViewAndAdapter(iCardShow2.iPageView pageView, iCardShow2.iDataAdapter dataAdapter) {
        this.pageView = pageView;
        this.dataAdapter = dataAdapter;
    }

    @Override
    public void unbindViewAndAdapter() {
        // Вроде как, присвоение null должно производиться в обратном bindViewAndAdapter() порядке.
        this.dataAdapter = new DataAdapter_Stub();
        this.pageView = new CardShow2_ViewStub();
    }

    @Override
    public void onPageOpened(String cardKey) {

        dataAdapter.showCardThrobber();

        cardsSingleton.loadCard(cardKey, new iCardsSingleton.LoadCallbacks() {
            @Override
            public void onCardLoadSuccess(Card card) {
                currentCard = card;
                pageView.setPageTitle(R.string.CARD_SHOW_page_title_long, card.getTitle());
                dataAdapter.showCard(card);
                loadComments(card.getKey(), null, null);
            }

            @Override
            public void onCardLoadFailed(String msg) {
                pageView.showErrorMsg(R.string.CARD_SHOW_error_displaying_card, msg);
            }
        });
    }

    @Override
    public void onLoadMoreClicked(iList_Item listItem) {
        int position = dataAdapter.getIndexOf(listItem);
        Comment previousComment = dataAdapter.getComment(position -1);
        Comment nextComment = dataAdapter.getComment(position +1);
        loadComments(currentCard.getKey(), previousComment, nextComment, position);
    }

    @Override
    public void onReplyClicked(iList_Item listItem)
    {
        if (AuthSingleton.isLoggedIn())
        {
            this.repliedItem = (iCommentable) listItem.getPayload();
            pageView.showCommentForm(repliedItem);
        }
        else {
            this.repliedItem = (iCommentable) listItem.getPayload();

            Bundle transitArguments = new Bundle();

            if (iList_Item.isCardItem(listItem)) {
                transitArguments.putParcelable(iCardShow2.REPLIED_OBJECT, (Card) this.repliedItem);
                transitArguments.putString(iCardShow2.REPLY_ACTION, iCardShow2.ACTION_REPLY_TO_CARD);
            }
            else if (iList_Item.isCommentItem(listItem)) {
                transitArguments.putParcelable(iCardShow2.REPLIED_OBJECT, (Comment) this.repliedItem);
                transitArguments.putString(iCardShow2.REPLY_ACTION, iCardShow2.ACTION_REPLY_TO_COMMENT);
            }
            else {
                throw new RuntimeException("Payload is instance of Card or Comment");
            }

            pageView.requestLogin(Constants.CODE_LOGIN_REQUEST, transitArguments);
        }
    }

    @Override
    public void onSendCommentClicked() {
        String commentText = pageView.getCommentText().trim();
        if (TextUtils.isEmpty(commentText))
            return;

        if (null == editedComment)
            createComment();
        else
            updateComment();
    }

    @Override
    public void onDeleteCommentClicked(iList_Item listItem, iCardShow2.iCommentViewHolder commentViewHolder) {
        Comment comment = (Comment) listItem.getPayload();

        if (!canAlterComment(comment)) {
            pageView.showToast(R.string.COMMENT_error_cannot_delete_this_comment);
            return;
        }

        MyDialogs.commentDeleteDialog(
                pageView.getActivity(),
                comment.getText(),
                new iMyDialogs.Delete() {
                    @Override
                    public void onCancelInDialog() {

                    }

                    @Override
                    public void onNoInDialog() {

                    }

                    @Override
                    public boolean onCheckInDialog() {
                        return true;
                    }

                    @Override
                    public void onYesInDialog() {
                        onDeleteCommentConfirmed(listItem, commentViewHolder);
                    }
                }
        );
    }

    @Override
    public void onDeleteCommentConfirmed(iList_Item listItem, iCardShow2.iCommentViewHolder commentViewHolder) {
        Comment comment = dataAdapter.getComment(listItem);

        if (!canAlterComment(comment))
            return;

        commentViewHolder.fadeBackground();

        commentsSingleton.deleteComment(comment, new iCommentsSingleton.DeleteCallbacks() {
            @Override
            public void onDeleteSuccess(Comment commentd) {
                dataAdapter.removeComment(listItem);
            }

            @Override
            public void onDeleteError(String msg) {
                pageView.showToast(R.string.COMMENT_delete_error);
                commentViewHolder.unfadeBackground();
            }
        });
    }

    @Override
    public void onEditCommentClicked(iList_Item listItem) {

        /*this.currentListItem = listItem;
        this.editedComment = (Comment) listItem.getPayload();

        if (!canAlterComment(this.editedComment)) {
            pageView.showToast(R.string.COMMENT_error_cannot_edit_this_comment);
            clearEditedElements();
            return;
        }

        pageView.showCommentForm(this.editedComment);*/

        Comment comment = (Comment) listItem.getPayload();
        pageView.startTestActivity(comment);
    }

    @Override
    public void onRemoveCommentQuoteClicked() {
        if (null != this.editedComment) {
            this.editedComment.removeParent();
        }
    }

    @Override
    public boolean canEditCard() {
        return canAlterCard();
    }

    @Override
    public boolean canDeleteCard() {
        return canAlterCard();
    }

    @Override
    public void processLoginRequest(Bundle transitArguments) throws IllegalArgumentException
    {
        String replyAction = transitArguments.getString(iCardShow2.REPLY_ACTION, "");
        iCommentable repliedObject = transitArguments.getParcelable(iCardShow2.REPLIED_OBJECT);

        switch (replyAction)
        {
            case iCardShow2.ACTION_REPLY_TO_CARD:
            case iCardShow2.ACTION_REPLY_TO_COMMENT:
                this.repliedItem = repliedObject;
                pageView.showCommentForm(repliedItem);
                break;

            default:
                throw new RuntimeException("Unknown replyAction: "+replyAction);
        }
    }


    // Внутренние методы
    private void loadComments(String cardKey, @Nullable Comment startAfterComment, @Nullable Comment endBoundaryComment) {
        loadComments(cardKey, startAfterComment, endBoundaryComment, null);
    }

    private void loadComments(String cardKey,
                              @Nullable Comment startAfterComment,
                              @Nullable Comment endBoundaryComment,
                              @Nullable Integer insertPosition)
    {
        dataAdapter.showCommentsThrobber(insertPosition);

        commentsSingleton.loadList(cardKey, startAfterComment, endBoundaryComment, new iCommentsSingleton.ListCallbacks() {
            @Override
            public void onCommentsLoadSuccess(List<Comment> list) {
                if (null != insertPosition)
                    dataAdapter.insertComments(list, insertPosition);
                else
                    dataAdapter.appendComments(list);
            }

            @Override
            public void onCommentsLoadError(String errorMessage) {
                pageView.showErrorMsg(R.string.COMMENTS_error_loading_comments, errorMessage);
            }
        });
    }

    private boolean canAlterCard() {
        User currentUser = usersSingleton.getCurrentUser();
        if (null == currentUser)
            return false;

        if (null == currentCard)
            return false;

        return currentCard.isCreatedBy(currentUser) || usersSingleton.currentUserIsAdmin();
    }

    private boolean canAlterComment(@Nullable Comment comment) {
        User currentUser = usersSingleton.getCurrentUser();

        if (null == currentUser)
            return false;

        if (null == comment)
            return false;

        return comment.isCreatedBy(currentUser.getKey()) || usersSingleton.currentUserIsAdmin();
    }

    private void createComment() {
        editedComment = new Comment(
                pageView.getCommentText(),
                currentCard.getKey(),
                this.repliedItem,
                usersSingleton.getCurrentUser()
        );

        pageView.disableCommentForm();

        commentsSingleton.createComment(editedComment, new iCommentsSingleton.CreateCallbacks() {
            @Override
            public void onCommentSaveSuccess(Comment comment) {
                clearEditedElements();

                int position = dataAdapter.appendOneComment(comment);
                pageView.scrollToComment(position);

                pageView.clearCommentForm();
                pageView.hideCommentForm();
            }

            @Override
            public void onCommentSaveError(String errorMsg) {
                pageView.showCommentFormError(R.string.COMMENT_error_adding_comment, errorMsg);
            }
        });
    }

    private void updateComment() {
        this.editedComment.updateCommentText(pageView.getCommentText());

        pageView.disableCommentForm();

        commentsSingleton.updateComment(editedComment, new iCommentsSingleton.CreateCallbacks() {
            @Override
            public void onCommentSaveSuccess(Comment comment) {
                dataAdapter.updateComment(currentListItem, comment);
                clearEditedElements(); // Это нужно делать после dataAdapter.updateComment()

                pageView.clearCommentForm();
                pageView.hideCommentForm();
            }

            @Override
            public void onCommentSaveError(String errorMsg) {
                pageView.showCommentFormError(R.string.COMMENT_error_saving_comment, errorMsg);
            }
        });
    }

    private void clearEditedElements() {
        this.currentListItem = null;
        this.repliedItem = null;
        this.editedComment = null;
    }
}
