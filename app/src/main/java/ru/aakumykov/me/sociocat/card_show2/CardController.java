package ru.aakumykov.me.sociocat.card_show2;

import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.interfaces.iCardsSingleton;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.singletons.CardsSingleton;

public class CardController implements iCardController {

    private iCardShow2_View view;


    @Override
    public void bindView(iCardShow2_View view) {
        this.view = view;
    }

    @Override
    public void unbindView() {
        this.view = null;
    }

    @Override
    public void loadCard(String cardKey, @Nullable String commentKey) {

        view.showProgressMessage(R.string.CARD_SHOW_loading_card);

        CardsSingleton.getInstance().loadCard(cardKey, new iCardsSingleton.LoadCallbacks() {
            @Override
            public void onCardLoadSuccess(Card card) {
                view.hideProgressMessage();

                view.displayCard(card);

//                commentsController.loadComments(card.getKey(), null, 10);
            }

            @Override
            public void onCardLoadFailed(String msg) {
                view.showErrorMsg(R.string.CARD_SHOW_error_loading_card, msg);
            }
        });

    }

}
