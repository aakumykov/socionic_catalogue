package ru.aakumykov.me.sociocat.card_show.controllers;

import ru.aakumykov.me.sociocat.models.Card;

public interface iCardController extends iController {

    interface LoadCardCallbacks {
        void onCardLoaded(Card card);
    }

    void loadCard(String cardKey, String commentKey, LoadCardCallbacks callbacks);

    void startCommentingCard(Card card);
}
