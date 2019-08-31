package ru.aakumykov.me.sociocat.card_show.presenters;

import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_show.adapter.CardView_Stub;
import ru.aakumykov.me.sociocat.card_show.iCardShow;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.models.User;
import ru.aakumykov.me.sociocat.singletons.CardsSingleton_CF;
import ru.aakumykov.me.sociocat.singletons.UsersSingleton_CF;
import ru.aakumykov.me.sociocat.singletons.iCardsSingleton;
import ru.aakumykov.me.sociocat.singletons.iUsersSingleton;
import ru.aakumykov.me.sociocat.utils.CardDeletionHelper;

public class CardPresenter implements iCardShow.iCardPresenter {

    private enum Rating {
        UP, DOWN
    }

    private final static String TAG = "CardPresenter";
    private iCardShow.iCardView cardView;
    private iCardShow.iPageView pageView;
    private iCardShow.iCommentsPresenter commentsPresenter;
//    private iCardsSingleton cardSingleton = CardsSingleton.getInstance();
    private iCardsSingleton cardSingleton = CardsSingleton_CF.getInstance();
    private iUsersSingleton usersSingleton = UsersSingleton_CF.getInstance();
    private @Nullable Card currentCard;


    public CardPresenter(iCardShow.iCommentsPresenter commentsPresenter) {
        this.commentsPresenter = commentsPresenter;
    }


    @Override public void bindPageView(iCardShow.iPageView pageView) {
        this.pageView = pageView;
    }

    @Override public void unbindPageView() {
        this.pageView = null;
    }

    @Override
    public void bindListAdapter(iCardShow.iCardView listAdapter) {
        this.cardView = (iCardShow.iCardView) listAdapter;
    }

    @Override
    public void unbindListAdapter() {
        this.cardView = new CardView_Stub();
    }

    @Override
    public void onWorkBegins(@Nullable Card card, @Nullable String commentKey) {

        if (null != card) {
            currentCard = card;

            try {
                cardView.displayCard(card);
                commentsPresenter.onWorkBegins(card.getKey(), commentKey);
            }
            catch (Exception e) {
                pageView.showErrorMsg(R.string.CARD_SHOW_error_displaying_card, e.getMessage());
                e.printStackTrace();
            }
        }
        else {
            pageView.showErrorMsg(R.string.CARD_SHOW_there_is_no_card, "Card is NULL");
        }
    }

    @Override
    public void onErrorOccurs() {
        cardView.hideCardThrobber();
    }


    @Override
    public void onReplyClicked() {
        commentsPresenter.onReplyClicked(currentCard);
    }

    @Override
    public Card getCard() {
        return currentCard;
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
    public void onEditClicked() {
        if (canAlterCard()) {
            pageView.goEditCard(currentCard);
        } else {
            pageView.showToast(R.string.CARD_SHOW_you_cannot_edit_this_card);
        }
    }

    @Override
    public void onCardEdited(Card card) {
        try {
            cardView.displayCard(card);
        }
        catch (Exception e) {
            pageView.showErrorMsg(R.string.CARD_SHOW_error_displaying_card, e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onDeleteClicked() {
        cardView.showCardDeleteDialog(currentCard);
    }

    @Override
    public void onDeleteConfirmed() {
        pageView.showProgressMessage(R.string.deleting_card);

        CardDeletionHelper.deleteCard(currentCard, new CardDeletionHelper.iDeletionCallbacks() {
            @Override
            public void onCardDeleteSuccess(Card card) {
                pageView.hideProgressMessage();
                pageView.showToast(R.string.card_deleted);
                pageView.closePageAfterDeletion(card);
            }

            @Override
            public void onCardDeleteError(String errorMsg) {
                pageView.showErrorMsg(R.string.CARD_SHOW_error_deleting_card, errorMsg);
            }
        });
    }

    @Override
    public void onTagClicked(String tag) {
        pageView.goShowCardsWithTag(tag);
    }

    @Override
    public void onRatingUpClicked(iCardShow.iCard_ViewHolder cardViewHolder) {
        changeCardRating(Rating.UP, cardViewHolder);
    }

    @Override
    public void onRatingDownClicked(iCardShow.iCard_ViewHolder cardViewHolder) {
        changeCardRating(Rating.DOWN, cardViewHolder);
    }

    @Override
    public void onSwipeRefreshRequested() {

        cardSingleton.loadCard(currentCard.getKey(), new iCardsSingleton.LoadCallbacks() {
            @Override
            public void onCardLoadSuccess(Card card) {
                pageView.hideSwipeRefreshThrobber();

                try {
                    cardView.displayCard(card);
                }
                catch (Exception e) {
                    pageView.showErrorMsg(R.string.CARD_SHOW_error_displaying_card, e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onCardLoadFailed(String errorMsg) {
                pageView.hideSwipeRefreshThrobber();
                pageView.showErrorMsg(R.string.CARD_SHOW_error_loading_card, errorMsg);
            }
        });
    }

    @Override
    public void onAuthorClicked() {
        pageView.goToAuthorProfile();
    }


    // Внутренние методы
    private boolean canAlterCard() {
        User currentUser = usersSingleton.getCurrentUser();
        if (null == currentUser)
            return false;

        if (null == currentCard)
            return false;

        return currentCard.isCreatedBy(currentUser) || usersSingleton.currentUserIsAdmin();
    }

    private void changeCardRating(Rating rating, iCardShow.iCard_ViewHolder cardViewHolder) {
        User user = usersSingleton.getCurrentUser();

        if (null == user) {
            pageView.showToast(R.string.CARD_SHOW_login_required_to_change_rating);
            return;
        }

        String userId = user.getKey();
        String cardId = currentCard.getKey();

        changeCardRating_Produce(cardId, userId, rating, cardViewHolder);
    }

    private void changeCardRating_Produce(
            String cardId,
            String userId,
            Rating rating,
            iCardShow.iCard_ViewHolder cardViewHolder
    )
    {
        iCardsSingleton.RatingCallbacks ratingCallbacks = new iCardsSingleton.RatingCallbacks() {
            @Override
            public void onRatedUp(Card ratedCard, int newRating) {
                currentCard = ratedCard;
                cardViewHolder.showRating(ratedCard, userId);
            }

            @Override
            public void onRatedDown(Card ratedCard, int newRating) {
                currentCard = ratedCard;
                cardViewHolder.showRating(ratedCard, userId);
            }

            @Override
            public void onRateFail(String errorMsg) {
                cardViewHolder.hideRatingThrobber();
                pageView.showToast(R.string.CARD_SHOW_error_changing_card_rating);
            }
        };

        // Проверяю, ставил ли этот пользователь рейтинг такого направления
        switch (rating) {
            case UP:
                if (currentCard.isRatedUpBy(userId))
                    return;
                break;
            case DOWN:
                if (currentCard.isRatedDownBy(userId))
                    return;
                break;
            default:
                throw new IllegalArgumentException("Wrong rating argument: "+rating);
        }

        // Меняю рейтинг
        cardViewHolder.showRatingThrobber();

        switch (rating) {
            case UP:
                cardSingleton.rateUp(cardId, userId, ratingCallbacks);
                break;

            case DOWN:
                cardSingleton.rateDown(cardId, userId, ratingCallbacks);
                break;

            default:
                throw new IllegalArgumentException("Wrong rating argument: "+rating);
        }
    }
}
