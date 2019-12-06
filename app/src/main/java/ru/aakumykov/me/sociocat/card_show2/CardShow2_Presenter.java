package ru.aakumykov.me.sociocat.card_show2;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.List;

import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_show2.list_items.iList_Item;
import ru.aakumykov.me.sociocat.card_show2.stubs.CardShow2_ViewStub;
import ru.aakumykov.me.sociocat.card_show2.stubs.DataAdapter_Stub;
import ru.aakumykov.me.sociocat.card_show2.view_holders.Card_ViewHolder;
import ru.aakumykov.me.sociocat.card_show2.view_holders.iCard_ViewHolder;
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

    private enum RatingChange {
        INCREASE, DECREASE
    }

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
        loadData(cardKey, new iLoadDataCallbacks() {
            @Override
            public void onLoadDataComplete() {

            }
        });
    }

    @Override
    public void onCardAlmostDisplayed(Card_ViewHolder cardViewHolder) {
        colorizeCardRatingWidgets(cardViewHolder);
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
        if (!AuthSingleton.isLoggedIn()) {
            pageView.showToast(R.string.CARD_SHOW_login_required_to_comment);
            return;
        }

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

        this.currentListItem = listItem;
        this.editedComment = (Comment) listItem.getPayload();

        if (!canAlterComment(this.editedComment)) {
            pageView.showToast(R.string.COMMENT_error_cannot_edit_this_comment);
            clearEditedElements();
            return;
        }

        pageView.showCommentForm(this.editedComment);
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

    @Override
    public void onRateUpClicked(iCard_ViewHolder cardViewHolder) {
        changeCardRating(true, cardViewHolder);
    }

    @Override
    public void onRateDownClicked(iCard_ViewHolder cardViewHolder) {
        changeCardRating(false, cardViewHolder);
    }

    @Override
    public void onRefreshRequested() {
        loadData(currentCard.getKey(), new iLoadDataCallbacks() {
            @Override
            public void onLoadDataComplete() {
                pageView.hideSwipeThrobber();
            }
        });
    }

    @Override
    public void onAuthorClicked() {
        pageView.showUserProfile(currentCard.getUserId());
    }


    // Внутренние методы
    private void loadData(String cardKey, iLoadDataCallbacks callbacks) {
        cardsSingleton.loadCard(cardKey, new iCardsSingleton.LoadCallbacks() {
            @Override
            public void onCardLoadSuccess(Card card) {
                callbacks.onLoadDataComplete();

                currentCard = card;
                pageView.setPageTitle(R.string.CARD_SHOW_page_title_long, card.getTitle());
                dataAdapter.showCard(card);

                loadComments(card.getKey(), null, null);
            }

            @Override
            public void onCardLoadFailed(String msg) {
                callbacks.onLoadDataComplete();

                pageView.showErrorMsg(R.string.CARD_SHOW_error_displaying_card, msg);
            }
        });
    }

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

    private void colorizeCardRatingWidgets(iCard_ViewHolder cardViewHolder) {
        if (!AuthSingleton.isLoggedIn())
            return;

        String cardKey = currentCard.getKey();
        User user = usersSingleton.getCurrentUser();

        if (user.alreadyRateUpCard(cardKey)) {
            cardViewHolder.setCardRatedUp();
        }
        else if (user.alreadyRateDownCard(cardKey)) {
            cardViewHolder.setCardRatedDown();
        }
        else {
            cardViewHolder.setCardNotRated();
        }
    }

    private void changeCardRating(boolean trueUpFalseDown, iCard_ViewHolder cardViewHolder) {
        // Игнорирую гостей
        if (!AuthSingleton.isLoggedIn()) {
            pageView.showToast(R.string.CARD_SHOW_login_required_to_change_rating);
            return;
        }

        // Подготавливаю инвентарь
        User user = usersSingleton.getCurrentUser();
        String cardKey = currentCard.getKey();
        iCardsSingleton.CardRatingStatus cardRatingStatus;

        // Определяю направление изменения рейтинга
        if (trueUpFalseDown) { // повышение
            if (user.alreadyRateUpCard(cardKey))
                return;

            cardRatingStatus = user.alreadyRateDownCard(cardKey) ?
                    iCardsSingleton.CardRatingStatus.UNRATED_DOWN :
                    iCardsSingleton.CardRatingStatus.RATED_UP;
        }
        else { // понижение
            if (user.alreadyRateDownCard(cardKey))
                return;

            cardRatingStatus = user.alreadyRateUpCard(cardKey) ?
                    iCardsSingleton.CardRatingStatus.UNRATED_UP :
                    iCardsSingleton.CardRatingStatus.RATED_DOWN;
        }

        // Изменяю рейтинг
        cardViewHolder.disableRatingControls();

        cardsSingleton.changeCardRating(
                cardRatingStatus,
                currentCard,
                user.getKey(),
                new iCardsSingleton.ChangeRatingCallbacks() {
                    @Override
                    public void onRatingChangeComplete(int value, @Nullable String errorMsg) {

                        switch (cardRatingStatus) {
                            case RATED_UP:
                                user.addRatedUpCard(cardKey);
                                break;
                            case UNRATED_UP:
                                user.removeRatedUpCard(cardKey);
                                break;
                            case RATED_DOWN:
                                user.addRatedDownCard(cardKey);
                                break;
                            case UNRATED_DOWN:
                                user.removeRatedDownCard(cardKey);
                                break;
                            default:
                                Log.e(TAG, "Unknown CardRatingStatus value: "+cardRatingStatus);
                                break;
                        }

                        // TODO: или проще обновлять пользователя с сервера?

                        currentCard.setRating(value);

                        cardViewHolder.setRating(value);
                        cardViewHolder.enableRatingControls();
                        colorizeCardRatingWidgets(cardViewHolder);

                        if (null != errorMsg)
                            pageView.showToast(R.string.CARD_SHOW_error_changing_card_rating);
                    }
                }
        );
    }


    private interface iLoadDataCallbacks {
        void onLoadDataComplete();
    }
}
