package ru.aakumykov.me.sociocat.singletons;

import androidx.annotation.Nullable;

import java.util.List;

import ru.aakumykov.me.sociocat.models.Card;

public interface iCardsSingleton {

    enum SortOrder {
        DIRECT, REVERSED
    }

    enum FilterOperator {
        EQUALS,
        GREATER,
        GREATER_OR_EQUALS,
        LOWER,
        LOWER_OR_EQUALS
    }

    enum CardRatingStatus {
        RATED_UP,
        UNRATED_UP,
        RATED_DOWN,
        UNRATED_DOWN
    }


    void loadCards(ListCallbacks callbacks);
    void loadCardsAfter(Card cardToLoadAfter, ListCallbacks callbacks);
    void loadCardsWithTag(String tagName, ListCallbacks callbacks);
    void loadCardsWithTagAfter(String tagName, Card cardToLoadAfter, ListCallbacks callbacks);
    void loadCardsFromNewestTo(Card endAtCard, ListCallbacks callbacks);
    void loadCardsWithTagFromNewestTo(String tag, Card endAtCard, ListCallbacks callbacks);

    void loadCard(String cardKey, LoadCallbacks callbacks);
    void updateCommentsCounter(String cardId, int diffValue);

    String createKey();
    void saveCard(Card card, @Nullable Card oldCard, SaveCardCallbacks callbacks);

    void deleteCard(Card card, DeleteCallbacks callbacks);

    @Deprecated
    void setRatedUp(String cardId, String byUserId, RatingCallbacks callbacks);
    @Deprecated
    void setRatedDown(String cardId, String byUserId, RatingCallbacks callbacks);

    void changeCardRating(CardRatingStatus cardRatingStatus, Card card, String userId, ChangeRatingCallbacks callbacks);



    interface ListCallbacks {
        // TODO: добавить в успешный колбек соообщение о сопутствующей ошибке...
        void onListLoadSuccess(List<Card> list);
        void onListLoadFail(String errorMessage);
    }

    interface LoadCallbacks {
        void onCardLoadSuccess(Card card);
        void onCardLoadFailed(String msg);
    }

    interface SaveCardCallbacks {
        void onCardSaveSuccess(Card card);
        void onCardSaveError(String message);
    }

    interface DeleteCallbacks {
        void onCardDeleteSuccess(Card card);
        void onCardDeleteError(String msg);
    }

    @Deprecated
    interface RatingCallbacks {
        void onRatedUp(Card ratedCard, int newRating);
        void onRatedDown(Card ratedCard, int newRating);
        void onRateFail(String errorMsg);
    }

    interface ChangeRatingCallbacks {
        void onRatingChangeComplete(int value, @Nullable String errorMsg);
    }
}
