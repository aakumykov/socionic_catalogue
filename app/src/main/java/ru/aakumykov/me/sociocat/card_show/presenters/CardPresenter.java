package ru.aakumykov.me.sociocat.card_show.presenters;

import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_show.adapter.iListAdapter_Card;
import ru.aakumykov.me.sociocat.singletons.iCardsSingleton;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.singletons.CardsSingleton;

public class CardPresenter implements iCardPresenter {

    private iListAdapter_Card listAdapter;
    private iCommentsPresenter commentsPresenter;
    private CardsSingleton cardSingleton = CardsSingleton.getInstance();

    public CardPresenter(iCommentsPresenter commentsPresenter) {
        this.commentsPresenter = commentsPresenter;
    }


    @Override
    public void bindListAdapter(iListAdapter_Card listAdapter) {
        this.listAdapter = (iListAdapter_Card) listAdapter;
    }

    @Override
    public void unbindListAdapter() {
        this.listAdapter = null;
    }


    @Override
    public void onWorkBegins(@Nullable String cardKey, @Nullable String commentKey) {

//        if (null == cardKey) {
//            listAdapter.showCardError(R.string.CARD_SHOW_error_loading_card, "cardKey is null");
//            return;
//        }

        listAdapter.showCardThrobber();

        cardSingleton.loadCard(cardKey, new iCardsSingleton.LoadCallbacks() {
            @Override
            public void onCardLoadSuccess(Card card) {
                listAdapter.hideCardThrobber();
                listAdapter.setCard(card);
            }

            @Override
            public void onCardLoadFailed(String msg) {
                listAdapter.hideCardThrobber();
                listAdapter.showCardError(R.string.CARD_SHOW_error_loading_card, msg);
            }
        });
    }

    @Override
    public void onAddCommentClicked() {

    }
}
