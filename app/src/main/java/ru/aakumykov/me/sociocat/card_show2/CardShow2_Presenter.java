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
import ru.aakumykov.me.sociocat.card_show2.view_holders.iComment_ViewHolder;
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

public class CardShow2_Presenter implements iCardShow2.iPresenter
{
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

        loadCard(cardKey, new iLoadCardCallbacks() {
            @Override
            public void onCardLoaded(Card card) {
                currentCard = card;
                pageView.setPageTitle(R.string.CARD_SHOW_page_title_long, card.getTitle());
                dataAdapter.showCard(card);

                loadComments(cardKey, null,null);
            }
        });
    }

    @Override
    public void onRefreshRequested() {

        loadCard(currentCard.getKey(), new iLoadCardCallbacks() {
            @Override
            public void onCardLoaded(Card card) {
                pageView.hideSwipeThrobber();

                currentCard = card;
                pageView.setPageTitle(R.string.CARD_SHOW_page_title_long, card.getTitle());
                dataAdapter.showCard(card);

                dataAdapter.clearCommentsList();
                loadComments(currentCard.getKey(), null, null);
            }
        });
    }

    @Override
    public void onCardAlmostDisplayed(Card_ViewHolder cardViewHolder) {
        colorizeCardRatingWidgets(cardViewHolder);
    }

    @Override
    public void onAuthorClicked() {
        pageView.showUserProfile(currentCard.getUserId());
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
    public void onDeleteCommentClicked(iList_Item listItem, iComment_ViewHolder commentViewHolder) {
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
    public void onDeleteCommentConfirmed(iList_Item listItem, iComment_ViewHolder commentViewHolder) {
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
    public void onCardRateUpClicked(iCard_ViewHolder cardViewHolder) {
        changeCardRating(true, cardViewHolder);
    }

    @Override
    public void onCardRateDownClicked(iCard_ViewHolder cardViewHolder) {
        changeCardRating(false, cardViewHolder);
    }

    @Override
    public void onCommentRateUpClicked(iComment_ViewHolder commentViewHolder, iList_Item commentItem) {
        changeCommentRating(true, commentViewHolder, commentItem);
    }

    @Override
    public void onCommentRateDownClicked(iComment_ViewHolder commentViewHolder, iList_Item commentItem) {
        changeCommentRating(false, commentViewHolder, commentItem);
    }


    // Внутренние методы
    private void loadCard(String cardKey, iLoadCardCallbacks callbacks) {
        cardsSingleton.loadCard(cardKey, new iCardsSingleton.LoadCallbacks() {
            @Override
            public void onCardLoadSuccess(Card card) {
                callbacks.onCardLoaded(card);
            }

            @Override
            public void onCardLoadFailed(String msg) {
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
        if (null == insertPosition)
            dataAdapter.showCommentsThrobber2();
        else
            dataAdapter.showCommentsThrobber2(insertPosition);

        commentsSingleton.loadList(cardKey, startAfterComment, endBoundaryComment, new iCommentsSingleton.ListCallbacks() {
            @Override
            public void onCommentsLoadSuccess(List<Comment> list) {
                if (null == insertPosition) {
                    dataAdapter.hideCommentsThrobber2();
                    dataAdapter.addCommentsList(list);
                } else {
                    dataAdapter.hideCommentsThrobber2(insertPosition);
                    dataAdapter.addCommentsList(list, insertPosition);
                }
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
        iCardsSingleton.CardRatingAction cardRatingAction;

        // Определяю направление изменения рейтинга
        if (trueUpFalseDown) { // повышение
            if (user.alreadyRateUpCard(cardKey))
                return;

            cardRatingAction = user.alreadyRateDownCard(cardKey) ?
                    iCardsSingleton.CardRatingAction.UNRATE_DOWN :
                    iCardsSingleton.CardRatingAction.RATE_UP;
        }
        else { // понижение
            if (user.alreadyRateDownCard(cardKey))
                return;

            cardRatingAction = user.alreadyRateUpCard(cardKey) ?
                    iCardsSingleton.CardRatingAction.UNRATE_UP :
                    iCardsSingleton.CardRatingAction.RATE_DOWN;
        }

        // Изменяю рейтинг
        cardViewHolder.disableRatingControls();

        cardsSingleton.changeCardRating(
                cardRatingAction,
                currentCard,
                user.getKey(),
                new iCardsSingleton.ChangeRatingCallbacks() {
                    @Override
                    public void onRatingChangeComplete(int value, @Nullable String errorMsg) {

                        switch (cardRatingAction) {
                            case RATE_UP:
                                user.addRatedUpCard(cardKey);
                                break;
                            case UNRATE_UP:
                                user.removeRatedUpCard(cardKey);
                                break;
                            case RATE_DOWN:
                                user.addRatedDownCard(cardKey);
                                break;
                            case UNRATE_DOWN:
                                user.removeRatedDownCard(cardKey);
                                break;
                            default:
                                Log.e(TAG, "Unknown CardRatingAction value: "+ cardRatingAction);
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

    private void changeCommentRating(boolean trueUpFalseDown, iComment_ViewHolder commentViewHolder, iList_Item commentItem) {
        // Отпинываю неавторизованных
        if (!AuthSingleton.isLoggedIn()) {
            pageView.showToast(R.string.CARD_SHOW_login_required_to_change_rating);
            return;
        }

        // Готовлю инвентарь
        Comment comment = (Comment) commentItem.getPayload();
        String commentKey = comment.getKey();
        User user = usersSingleton.getCurrentUser();
        iCommentsSingleton.CommentRatingAction commentRatingAction;

        // Определяю направление изменения рейтинга
        if (trueUpFalseDown) {
            if (user.alreadyRateUpComment(commentKey)) {
                return;
            }
            else if (user.alreadyRateDownComment(commentKey)) {
                commentRatingAction = iCommentsSingleton.CommentRatingAction.UNRATE_DOWN;
            }
            else {
                commentRatingAction = iCommentsSingleton.CommentRatingAction.RATE_UP;
            }
        }
        else {
            if (user.alreadyRateDownComment(commentKey)) {
                return;
            }
            else if (user.alreadyRateUpComment(commentKey)) {
                commentRatingAction = iCommentsSingleton.CommentRatingAction.UNRATE_UP;
            }
            else {
                commentRatingAction = iCommentsSingleton.CommentRatingAction.RATE_DOWN;
            }
        }

        // Изменяю рейтинг
        commentViewHolder.disableRatingControls();

        commentsSingleton.changeCommentRating(
                commentRatingAction,
                comment,
                user.getKey(),
                new iCommentsSingleton.ChangeRatingCallbacks() {
                    @Override
                    public void onRatingChangeComplete(int value, @Nullable String errorMsg) {
                        switch (commentRatingAction) {
                            case RATE_UP:
                                user.addRatedUpCommentKey(commentKey);
                                break;
                            case UNRATE_UP:
                                user.removeRatedUpCommentKey(commentKey);
                                break;
                            case RATE_DOWN:
                                user.addRatedDownCommentKey(commentKey);
                                break;
                            case UNRATE_DOWN:
                                user.removeRatedDownCommentKey(commentKey);
                                break;
                            default:
                                Log.e(TAG, "Unknown commentRatingAction value: "+commentRatingAction);
                        }

                        commentViewHolder.setRating(value);
                        commentViewHolder.enablRatingControls();

                        commentViewHolder.colorizeRatingWidget(commentRatingAction);

                        if (null != errorMsg)
                            pageView.showToast(R.string.COMMENT_error_cannot_change_comment_rating);
                    }
                }
        );

    }


    private interface iLoadCardCallbacks {
        void onCardLoaded(Card card);
    }

    private interface iLoadDataCallbacks {
        void onLoadDataComplete();
    }
}
