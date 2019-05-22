package ru.aakumykov.me.sociocat.card_show.presenters;

import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_show.adapter.iListAdapter_Card;
import ru.aakumykov.me.sociocat.card_show.iReplyView;
import ru.aakumykov.me.sociocat.singletons.iCardsSingleton;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.singletons.CardsSingleton;

public class CardPresenter implements iCardPresenter {

    private iReplyView replyView;
    private iListAdapter_Card listAdapter;
    private iCommentsPresenter commentsPresenter;
    private CardsSingleton cardSingleton = CardsSingleton.getInstance();
    private Card currentCard;


    public CardPresenter(iCommentsPresenter commentsPresenter) {
        this.commentsPresenter = commentsPresenter;
    }


    @Override
    public void bindViewAdapter(iListAdapter_Card listAdapter) {
        this.listAdapter = (iListAdapter_Card) listAdapter;
    }

    @Override
    public void unbindViewAdapter() {
        this.listAdapter = null;
    }

    @Override
    public void bindReplyView(iReplyView replyView) {
        this.replyView = replyView;
    }

    @Override
    public void unbindReplyView() {
        this.replyView = null;
    }

    @Override
    public void onWorkBegins(@Nullable String cardKey, @Nullable String commentKey) {

        listAdapter.showCardThrobber();

        cardSingleton.loadCard(cardKey, new iCardsSingleton.LoadCallbacks() {
            @Override
            public void onCardLoadSuccess(Card card) {
                currentCard = card;

                listAdapter.hideCardThrobber();
                listAdapter.setCard(card);

                commentsPresenter.onWorkBegins(cardKey, commentKey);
            }

            @Override
            public void onCardLoadFailed(String msg) {
                listAdapter.hideCardThrobber();
                listAdapter.showCardError(R.string.CARD_SHOW_error_loading_card, msg);
            }
        });
    }

    @Override
    public void onErrorOccurs() {
        listAdapter.hideCardThrobber();
    }


    @Override
    public void replyClicked() {
        replyView.showCommentForm(null, currentCard);
    }
}
