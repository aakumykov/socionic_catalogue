package ru.aakumykov.me.sociocat.card_show2;

import java.util.List;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.models.Comment;
import ru.aakumykov.me.sociocat.singletons.CardsSingleton;
import ru.aakumykov.me.sociocat.singletons.CommentsSingleton;
import ru.aakumykov.me.sociocat.singletons.iCardsSingleton;
import ru.aakumykov.me.sociocat.singletons.iCommentsSingleton;

public class CardShow2_Presenter implements iCardShow2.iPresenter {

    private final static String TAG = "CardShow2_Presenter";
    private CardsSingleton cardsSingleton = CardsSingleton.getInstance();
    private CommentsSingleton commentsSingleton = CommentsSingleton.getInstance();
    private iCardShow2.iPageView pageView = null;
    private iCardShow2.iDataAdapter dataAdapter = null;
    private Card currentCard = null;


    // iCardShow2.iPresenter
    @Override
    public void bindView(iCardShow2.iPageView view) {
        this.pageView = view;
    }

    @Override
    public void unbindView() {
        this.pageView = new CardShow2_ViewStub();
    }

    @Override
    public void bindDataAdapter(iCardShow2.iDataAdapter dataAdapter) {
        this.dataAdapter = dataAdapter;
    }

    @Override
    public void unbindDataAdapter() {
        this.dataAdapter = null;
    }

    @Override
    public void onPageOpened(String cardKey) {
        dataAdapter.showCardThrobber();

        cardsSingleton.loadCard(cardKey, new iCardsSingleton.LoadCallbacks() {
            @Override
            public void onCardLoadSuccess(Card card) {
                dataAdapter.showCard(card);
            }

            @Override
            public void onCardLoadFailed(String msg) {
                pageView.showErrorMsg(R.string.CARD_SHOW_error_displaying_card, msg);
            }
        });
    }

    @Override
    public void onLoadMoreClicked() {

    }
}
