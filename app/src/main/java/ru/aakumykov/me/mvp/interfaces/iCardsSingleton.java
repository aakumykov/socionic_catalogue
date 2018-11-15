package ru.aakumykov.me.mvp.interfaces;

import java.util.List;

import ru.aakumykov.me.mvp.models.Card;

public interface iCardsSingleton {

    void loadList(ListCallbacks callbacks);
    void loadList(String tagFilter, ListCallbacks callbacks);

    void loadCard(String key, LoadCallbacks callbacks);
    void updateCommentsCounter(String cardId, int diffValue);

    String createKey();
    void updateCard(Card card, SaveCardCallbacks callbacks);
    void deleteCard(Card card, DeleteCallbacks callbacks);


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
}
