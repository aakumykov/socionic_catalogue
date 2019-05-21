package ru.aakumykov.me.sociocat.card_show.presenters;

import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_show.adapter.iCard_ViewAdapter;
import ru.aakumykov.me.sociocat.card_show.iCommentForm;
import ru.aakumykov.me.sociocat.singletons.iCardsSingleton;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.singletons.CardsSingleton;

public class CardPresenter implements iCardPresenter {

    private iCommentForm commentForm;
    private iCard_ViewAdapter listAdapter;
    private iCommentsPresenter commentsPresenter;
    private CardsSingleton cardSingleton = CardsSingleton.getInstance();

    public CardPresenter(iCommentsPresenter commentsPresenter) {
        this.commentsPresenter = commentsPresenter;
    }


    @Override public void bindCommentForm(iCommentForm pageView) {
        this.commentForm = pageView;
    }

    @Override public void unbindCommentForm() {
        this.commentForm = null;
    }

    @Override
    public void bindListAdapter(iCard_ViewAdapter listAdapter) {
        this.listAdapter = (iCard_ViewAdapter) listAdapter;
    }

    @Override
    public void unbindListAdapter() {
        this.listAdapter = null;
    }


    @Override
    public void onWorkBegins(@Nullable String cardKey, @Nullable String commentKey) {

        listAdapter.showCardThrobber();

        cardSingleton.loadCard(cardKey, new iCardsSingleton.LoadCallbacks() {
            @Override
            public void onCardLoadSuccess(Card card) {
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
    public void onAddCommentClicked() {

    }
}
