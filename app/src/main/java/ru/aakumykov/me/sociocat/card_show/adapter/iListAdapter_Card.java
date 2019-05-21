package ru.aakumykov.me.sociocat.card_show.adapter;

import ru.aakumykov.me.sociocat.models.Card;

public interface iListAdapter_Card {

    void showCardThrobber();
    void hideCardThrobber();

    void showCardError(int errorMsgId, String errorMsg);
    void hideCardError();

    void setCard(Card card);
}
