package ru.aakumykov.me.sociocat.card_show.event_objects;

import androidx.annotation.NonNull;

import ru.aakumykov.me.sociocat.models.Card;

public class CardEditedEvent {

    private final Card mOldCard;
    private final Card mNewCard;

    public CardEditedEvent(@NonNull Card oldCard, @NonNull Card newCard) {
        mOldCard = oldCard;
        mNewCard = newCard;
    }

    public Card getOldCard() {
        return mOldCard;
    }

    public Card getNewCard() {
        return mNewCard;
    }
}
