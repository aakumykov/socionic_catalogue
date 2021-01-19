package io.gitlab.aakumykov.sociocat.singletons;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.firestore.CollectionReference;

import java.util.List;

import io.gitlab.aakumykov.sociocat.models.Card;
import io.gitlab.aakumykov.sociocat.models.User;

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

    enum CardRatingAction {
        RATE_UP,
        UNRATE_UP,
        RATE_DOWN,
        UNRATE_DOWN
    }

    CollectionReference getCardsCollection();

    void checkCardExists(@NonNull String cardKey, CardCheckExistingCallbacks callbacks);

    void loadFirstPortion(ListCallbacks callbacks);
    void loadCardsAfter(Card cardToLoadAfter, ListCallbacks callbacks);
    void loadCardsWithTag(String tagName, ListCallbacks callbacks);
    void loadCardsWithTagAfter(String tagName, Card cardToLoadAfter, ListCallbacks callbacks);
    void loadCardsFromNewestTo(Card endAtCard, ListCallbacks callbacks);
    void loadCardsWithTagFromNewestTo(String tag, Card endAtCard, ListCallbacks callbacks);

    void loadCardsOfUser(@NonNull String userKey, @NonNull ListCallbacks callbacks);
    void loadCardsOfUserAfter(@NonNull Card card, @NonNull User userFilter, ListCallbacks callbacks);

    void loadAllCards(ListCallbacks callbacks);

    void loadCard(String cardKey, LoadCallbacks callbacks);
    void updateCommentsCounter(String cardId, int diffValue);

    String createKey();
    void saveCard(Card card, @Nullable Card oldCard, SaveCardCallbacks callbacks);

    void deleteCard(Card card, DeleteCallbacks callbacks);

    @Deprecated
    void setRatedUp(String cardId, String byUserId, RatingCallbacks callbacks);
    @Deprecated
    void setRatedDown(String cardId, String byUserId, RatingCallbacks callbacks);

    void changeCardRating(CardRatingAction cardRatingAction, Card card, String userId, ChangeRatingCallbacks callbacks);



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

    interface CardCheckExistingCallbacks {
        void onCardExists(@NonNull String cardKey);
        void onCardNotExists(@NonNull String notExistingCardKey);
    }
}
