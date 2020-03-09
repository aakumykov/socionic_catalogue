package ru.aakumykov.me.sociocat;

interface iMyApp {
    boolean isNewCardsAvailable();
    int getNewCardsCount();
    void resetNewCardsCounter();
}
