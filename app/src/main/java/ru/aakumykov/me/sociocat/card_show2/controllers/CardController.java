package ru.aakumykov.me.sociocat.card_show2.controllers;

import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_show2.iCardShow2_View;
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
    public void loadCard(String cardKey, @Nullable String commentKey, LoadCardCallbacks callbacks) {

        view.showProgressMessage(R.string.CARD_SHOW_loading_card);

        CardsSingleton.getInstance().loadCard(cardKey, new iCardsSingleton.LoadCallbacks() {
            @Override
            public void onCardLoadSuccess(Card card) {
                view.hideProgressMessage();
                view.displayCard(card);
                callbacks.onCardLoaded(card);
            }

            @Override
            public void onCardLoadFailed(String msg) {
                view.showErrorMsg(R.string.CARD_SHOW_error_loading_card, msg);
            }
        });

    }

}
