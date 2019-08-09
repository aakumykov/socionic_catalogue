package ru.aakumykov.me.sociocat.singletons;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.models.Card;

public class CardsSingleton_CF implements iCardsSingleton {

    private final static String TAG = "CardsSingleton_CF";
    private CollectionReference cardsCollection;

    /* Одиночка */
    private static volatile CardsSingleton_CF ourInstance;
    public synchronized static CardsSingleton_CF getInstance() {
        synchronized (CardsSingleton_CF.class) {
            if (null == ourInstance) ourInstance = new CardsSingleton_CF();
            return ourInstance;
        }
    }
    private CardsSingleton_CF() {
        cardsCollection = FirebaseFirestore.getInstance().collection(Constants.CARDS_PATH);
    }
    /* Одиночка */


    @Override
    public void loadList(ListCallbacks callbacks) {
        loadListEnhanced(
                Card.KEY_CTIME,
                SortOrder.DIRECT,
                null,
                null,
                null,
                null,
                null,
                null,
                callbacks
        );
    }

    @Override
    public void loadList(@Nullable String startKey, @Nullable String endKey, ListCallbacks callbacks) {
        throw new RuntimeException("Устаревший метод");
    }

    @Override
    public void loadCardsWithTag(String tagName, @Nullable String startKey, @Nullable String endKey, ListCallbacks callbacks) {

        loadListEnhanced(
                Card.KEY_CTIME,
                SortOrder.DIRECT,
                "tag",
                FilterOperator.EQUALS,
                "qwerty",
                null,
                null,
                null,
                callbacks
            );
    }

    @Override
    public void loadList(int limit, ListCallbacks callbacks) {

    }

    @Override
    public void loadList(String tagFilter, ListCallbacks callbacks) {

    }

    @Override
    public void loadListForUser(String userId, ListCallbacks callbacks) {

    }

    @Override
    public void loadNewCards(long newerThanTime, ListCallbacks callbacks) {

    }

    @Override
    public void loadCard(String cardKey, LoadCallbacks callbacks) {

        cardsCollection.document(cardKey).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        try {
                            Card card = documentSnapshot.toObject(Card.class);
                            callbacks.onCardLoadSuccess(card);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                            callbacks.onCardLoadFailed(e.getMessage());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        callbacks.onCardLoadFailed(e.getMessage());
                    }
                });

    }

    @Override
    public void updateCommentsCounter(String cardId, int diffValue) {

    }

    @Override
    public String createKey() {
        return null;
    }

    @Override
    public void saveCard(Card card, SaveCardCallbacks callbacks) {

        DocumentReference cardReference;

        if (null == card.getKey()) {
            cardReference = cardsCollection.document();
            card.setKey(cardReference.getId());
        }
        else {
            cardReference = cardsCollection.document(card.getKey());
        }

        cardReference.set(card)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callbacks.onCardSaveSuccess(card);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        callbacks.onCardSaveError(e.getMessage());
                    }
                });
    }

    @Override
    public void deleteCard(Card card, DeleteCallbacks callbacks) {

    }

    @Override
    public void rateUp(String cardId, String byUserId, RatingCallbacks callbacks) {

    }

    @Override
    public void rateDown(String cardId, String byUserId, RatingCallbacks callbacks) {

    }


    // Внутренниие методы
    private void loadListEnhanced(
            String orderKey,
            SortOrder sortOrder,

            String filterKey,
            FilterOperator filterOperator,
            String filterValue,

            String startAt,
            String endAt,
            Integer limit,

            ListCallbacks callbacks
    )
    {
        Query query = cardsCollection;

        // Сортировка
        if (null != orderKey) {
            Query.Direction orderDirection = (SortOrder.REVERSED.equals(sortOrder)) ?
                    Query.Direction.DESCENDING : Query.Direction.ASCENDING;
            query = query.orderBy(orderKey, orderDirection);
        }
        else
            query = query.orderBy(Card.KEY_CTIME, Query.Direction.DESCENDING);


        // Фильтрация
        if (null != filterKey) {
            switch (filterOperator) {
                case EQUALS:
                    query = query.whereEqualTo(filterKey, filterValue);
                    break;
                case GREATER:
                    query = query.whereGreaterThan(filterKey, filterValue);
                    break;
                case GREATER_OR_EQUALS:
                    query = query.whereGreaterThanOrEqualTo(filterKey, filterValue);
                    break;
                case LOWER:
                    query = query.whereLessThan(filterKey, filterValue);
                    break;
                case LOWER_OR_EQUALS:
                    query = query.whereLessThanOrEqualTo(filterKey, filterValue);
                default:
                    callbacks.onListLoadFail("Wrong filter operator: "+filterOperator);
                    return;
            }
        }

        query = query.whereArrayContains("tags", "йцукен");

        // Начальное значение
        if (null != startAt)
            query = query.startAt(startAt);


        // Конечное значение
        if (null != endAt)
            query = query.endAt(endAt);


        // Предельное количество
        if (null != limit)
            query = query.limit(limit);


        // Собственно запрос
        query.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        List<Card> cardsList = new ArrayList<>();
                        boolean cardsErrors = false;

                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                            if (documentSnapshot.exists()) {
                                try {
                                    Card card = documentSnapshot.toObject(Card.class);
                                    if (null != card)
                                        cardsList.add(card);
                                }
                                catch (Exception e) {
                                    Log.e(TAG, e.getMessage());
                                    e.printStackTrace();
                                    cardsErrors = true;
                                }
                            }
                        }

                        if (0 == cardsList.size() && cardsErrors)
                            callbacks.onListLoadFail("Error exception(s) on cards loading.");
                        else
                            callbacks.onListLoadSuccess(cardsList);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        callbacks.onListLoadFail(e.getMessage());
                    }
                });
    }
}
