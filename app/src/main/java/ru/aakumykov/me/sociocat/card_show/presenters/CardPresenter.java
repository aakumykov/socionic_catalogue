package ru.aakumykov.me.sociocat.card_show.presenters;

import android.util.Log;

import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_show.adapter.CardView_Stub;
import ru.aakumykov.me.sociocat.card_show.adapter.iCardView;
import ru.aakumykov.me.sociocat.card_show.iPageView;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.models.User;
import ru.aakumykov.me.sociocat.singletons.CardsSingleton;
import ru.aakumykov.me.sociocat.singletons.UsersSingleton;
import ru.aakumykov.me.sociocat.singletons.iCardsSingleton;
import ru.aakumykov.me.sociocat.singletons.iUsersSingleton;

public class CardPresenter implements iCardPresenter {

    private final static String TAG = "CardPresenter";
    private iCardView cardView;
    private iPageView pageView;
    private iCommentsPresenter commentsPresenter;
    private iCardsSingleton cardSingleton = CardsSingleton.getInstance();
    private iUsersSingleton usersSingleton = UsersSingleton.getInstance();
    private @Nullable Card currentCard;


    public CardPresenter(iCommentsPresenter commentsPresenter) {
        this.commentsPresenter = commentsPresenter;
    }


    @Override public void bindPageView(iPageView pageView) {
        this.pageView = pageView;
    }

    @Override public void unbindPageView() {
        this.pageView = null;
    }

    @Override
    public void bindListAdapter(iCardView listAdapter) {
        this.cardView = (iCardView) listAdapter;
    }

    @Override
    public void unbindListAdapter() {
        this.cardView = new CardView_Stub();
    }

    @Override
    public void onWorkBegins(@Nullable String cardKey, @Nullable String commentKey) {

        cardView.showCardThrobber();

        cardSingleton.loadCard(cardKey, new iCardsSingleton.LoadCallbacks() {
            @Override
            public void onCardLoadSuccess(Card card) {
                currentCard = card;
                pageView.refreshMenu();

                cardView.hideCardThrobber();

                try {
                    cardView.displayCard(card);
                    commentsPresenter.onWorkBegins(cardKey, commentKey);
                } catch (Exception e) {
                    pageView.showErrorMsg(R.string.CARD_SHOW_there_is_no_card, e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onCardLoadFailed(String msg) {
                cardView.hideCardThrobber();
                cardView.showCardError(R.string.CARD_SHOW_error_loading_card, msg);
            }
        });
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

        cardSingleton.deleteCard(currentCard, new iCardsSingleton.DeleteCallbacks() {
            @Override
            public void onCardDeleteSuccess(Card card) {
                pageView.hideProgressMessage();
                pageView.showToast(R.string.card_deleted);
                pageView.closePage();
            }

            @Override
            public void onCardDeleteError(String msg) {
                pageView.showErrorMsg(R.string.CARD_SHOW_error_deleting_card, msg);
            }
        });
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
}
