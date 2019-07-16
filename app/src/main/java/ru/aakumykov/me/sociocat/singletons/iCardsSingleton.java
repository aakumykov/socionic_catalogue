package ru.aakumykov.me.sociocat.singletons;

import androidx.annotation.Nullable;

import java.util.List;

import ru.aakumykov.me.sociocat.models.Card;

public interface iCardsSingleton {

    void loadList(@Nullable String startKey, @Nullable String endKey, ListCallbacks callbacks);
    void loadCardsWithTag(String tagName, @Nullable String startKey, @Nullable String endKey, ListCallbacks callbacks );
    void loadList(ListCallbacks callbacks);
    void loadList(int limit, ListCallbacks callbacks);
    void loadList(String tagFilter, ListCallbacks callbacks);
    void loadListForUser(String userId, ListCallbacks callbacks);
    void loadNewCards(long newerThanTime, ListCallbacks callbacks);

    void loadCard(String key, LoadCallbacks callbacks);
    void updateCommentsCounter(String cardId, int diffValue);

    String createKey();
    void saveCard(Card card, SaveCardCallbacks callbacks);
    void deleteCard(Card card, DeleteCallbacks callbacks);

    void rateUp(String cardId, String byUserId, RatingCallbacks callbacks);
    void rateDown(String cardId, String byUserId, RatingCallbacks callbacks);


    interface ListCallbacks {
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
        void onRetedUp(int newRating);
        void onRatedDown(int newRating);
        void onRateFail(String errorMsg);
    }
}
