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

    void rateUp(String cardId, String byUserId, RatingCallbacks callbacks);
    void rateDown(String cardId, String byUserId, RatingCallbacks callbacks);

    void rateUp(Card card, String userId, RatingChangeCallbacks callbacks);
    void rateDown(Card card, String userId, RatingChangeCallbacks callbacks);

    void getCardRating(String cardKey, GetCardRatingCallbacks callbacks);

    interface GetCardRatingCallbacks {
        void onGetCardRatingSuccess(int value);
        void onGetCardRatingError(String errorMsg);
    }


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

    interface RatingCallbacks {
        void onRatedUp(Card ratedCard, int newRating);
        void onRatedDown(Card ratedCard, int newRating);
        void onRateFail(String errorMsg);
    }

    interface RatingChangeCallbacks {
        void onRatingChangeComplete(int value, @Nullable String errorMsg);
    }
}
