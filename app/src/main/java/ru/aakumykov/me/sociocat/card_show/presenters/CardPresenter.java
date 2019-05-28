package ru.aakumykov.me.sociocat.card_show.presenters;

import androidx.annotation.Nullable;

import org.greenrobot.eventbus.Subscribe;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_show.adapter.CardView_Stub;
import ru.aakumykov.me.sociocat.card_show.adapter.iCardView;
import ru.aakumykov.me.sociocat.event_objects.LoginRequestSuccess;
import ru.aakumykov.me.sociocat.singletons.AuthSingleton;
import ru.aakumykov.me.sociocat.singletons.iCardsSingleton;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.singletons.CardsSingleton;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class CardPresenter implements iCardPresenter {

    private iCardView cardView;
    private iCommentsPresenter commentsPresenter;
    private CardsSingleton cardSingleton = CardsSingleton.getInstance();
    private Card currentCard;


    public CardPresenter(iCommentsPresenter commentsPresenter) {
        this.commentsPresenter = commentsPresenter;
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
        if (AuthSingleton.isLoggedIn())
            cardView.showCommentForm(currentCard);
        else
            MyUtils.requestLogin(cardView.getPageContext(), "add_comment");
    }


    // EventBus
    @Subscribe
    public void onLoginRequestSuccess(LoginRequestSuccess loginRequestSuccess) {
        String requestedAction = loginRequestSuccess.getRequestedAction();

        switch (requestedAction) {
            case "add_comment":
                cardView.showCommentForm(currentCard);
                break;
            default:
//                cardView.showToast("Неизвестное действие");
                break;
        }
    }

}
