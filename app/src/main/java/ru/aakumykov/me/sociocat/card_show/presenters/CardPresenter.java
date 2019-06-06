package ru.aakumykov.me.sociocat.card_show.presenters;

import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_show.adapter.CardAdapter_Stub;
import ru.aakumykov.me.sociocat.card_show.adapter.iCardAdapter;
import ru.aakumykov.me.sociocat.card_show.iPageView;
import ru.aakumykov.me.sociocat.singletons.iCardsSingleton;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.singletons.CardsSingleton;

public class CardPresenter implements iCardPresenter {

    private iCardAdapter cardView;
    private iPageView pageView;
    private iCommentsPresenter commentsPresenter;
    private CardsSingleton cardSingleton = CardsSingleton.getInstance();
    private Card currentCard;


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
    public void bindListAdapter(iCardAdapter listAdapter) {
        this.cardView = (iCardAdapter) listAdapter;
    }

    @Override
    public void unbindListAdapter() {
        this.cardView = new CardAdapter_Stub();
    }

    @Override
    public void onWorkBegins(@Nullable String cardKey, @Nullable String commentKey) {

        cardView.showCardThrobber();

        cardSingleton.loadCard(cardKey, new iCardsSingleton.LoadCallbacks() {
            @Override
            public void onCardLoadSuccess(Card card) {
                currentCard = card;

                cardView.hideCardThrobber();
                cardView.displayCard(card);

                commentsPresenter.onWorkBegins(cardKey, commentKey);
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
}
