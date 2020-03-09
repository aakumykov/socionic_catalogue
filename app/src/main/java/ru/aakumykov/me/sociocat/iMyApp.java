package ru.aakumykov.me.sociocat;

import java.util.List;

import ru.aakumykov.me.sociocat.models.Card;

interface iMyApp {
    boolean isNewCardsAvailable();
    List<Card> getNewCards();
}
