package ru.aakumykov.me.sociocat.card_show.presenters;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_show.adapter.iListAdapter;
import ru.aakumykov.me.sociocat.card_show.adapter.iListAdapter_Card;
import ru.aakumykov.me.sociocat.card_show.services.Card_Service;
import ru.aakumykov.me.sociocat.models.Card;

public class CardPresenter implements iCardPresenter {

    private iListAdapter_Card listAdapter;
    private iCommentsPresenter commentsPresenter;

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
    public void onWorkBegins() {

        listAdapter.showCardThrobber();

        new Card_Service().loadCard("", new Card_Service.iCardLoadCallbacks() {
            @Override
            public void onCardLoadSuccess(Card card) {
                listAdapter.hideCardThrobber();
                listAdapter.setCard(card);
            }

            @Override
            public void onCardLoadFail(String errorMsg) {
                listAdapter.hideCardThrobber();
                listAdapter.showCardError(R.string.CARD_SHOW_error_loading_card, errorMsg);
            }
        });
    }

    @Override
    public void onAddCommentClicked() {

    }
}
