package ru.aakumykov.me.sociocat.singletons;

import android.util.Log;

import androidx.annotation.NonNull;

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

import ru.aakumykov.me.sociocat.Config;
import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.models.Card;

public class CardsSingleton_CF implements iCardsSingleton {

    private final static String TAG = "CardsSingleton_CF";
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference cardsCollection;
    private CollectionReference tagsCollection;

    // Одиночка
    private static volatile CardsSingleton_CF ourInstance;
    public synchronized static CardsSingleton_CF getInstance() {
        synchronized (CardsSingleton_CF.class) {
            if (null == ourInstance) ourInstance = new CardsSingleton_CF();
            return ourInstance;
        }
    }
    private CardsSingleton_CF() {
        firebaseFirestore = FirebaseFirestore.getInstance();
        cardsCollection = firebaseFirestore.collection(Constants.CARDS_PATH);
        tagsCollection = firebaseFirestore.collection(Constants.TAGS_PATH);
    }
    // Одиночка


    @Override
    public void loadCards(ListCallbacks callbacks) {
        loadListEnhanced(
                null,
                null,
                null,
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
    public void loadCardsAfter(Card cardToLoadAfter, ListCallbacks callbacks) {
        loadListEnhanced(
                null,
                null,
                null,
                null,
                null,
                null,
                cardToLoadAfter.getKey(),
                null,
                null,
                callbacks
        );
    }

    @Override
    public void loadCardsWithTag(String tagName, ListCallbacks callbacks) {
        loadListEnhanced(
                null,
                null,
                tagName,
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
    public void loadCardsWithTagAfter(String tagName, Card cardToLoadAfter, ListCallbacks callbacks) {
        loadListEnhanced(
                null,
                null,
                tagName,
                null,
                null,
                null,
                cardToLoadAfter.getKey(),
                null,
                null,
                callbacks
        );
    }

    /*@Override
        public void loadList(ListCallbacks callbacks) {
            loadListEnhanced(
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    Config.DEFAULT_CARDS_LOAD_COUNT,
                    callbacks);
        }

        @Override
        public void loadListFromTo(@Nullable String startKey, @Nullable String endKey, ListCallbacks callbacks) {
            loadListEnhanced(
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    startKey,
                    endKey,
                    null,
                    callbacks
            );
        }

        @Override
        public void loadCardsWithTag(String tagName, @Nullable String startKey, @Nullable String endKey, ListCallbacks callbacks) {
            loadListEnhanced(
                    null,
                    null,
                    tagName,
                    null,
                    null,
                    null,
                    null,
                    null,
                    Config.DEFAULT_CARDS_LOAD_COUNT,
                    callbacks);
        }

        @Override
        public void loadCardsAfter(Card previousCard, @Nullable String tagFilter, ListCallbacks callbacks) {

            Query query = cardsCollection
                    .orderBy(Card.KEY_CTIME, Query.Direction.DESCENDING)
                    .startAfter(previousCard.getCTime())
                    .limit(Config.DEFAULT_CARDS_LOAD_COUNT);

            if (null != tagFilter)
                query = query.whereArrayContains(Card.KEY_TAGS, tagFilter);

            query.get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            List<Card> list = new ArrayList<>();
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments())
                                list.add(documentSnapshot.toObject(Card.class));
                            callbacks.onListLoadSuccess(list);
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

        @Override
        public void loadCardsFromNowTo(Card beforeCard, ListCallbacks callbacks) {

            Query query = cardsCollection
                    .orderBy(Card.KEY_CTIME, Query.Direction.DESCENDING)
                    .endAt(beforeCard.getCTime());

            query.get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            List<Card> cardsList = new ArrayList<>();
                            boolean error = false;
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                                try {
                                    Card card = documentSnapshot.toObject(Card.class);
                                    cardsList.add(card);
                                } catch (Exception e) {
                                    error = true;
                                    Log.e(TAG, e.getMessage());
                                    e.printStackTrace();
                                }
                            }
                            if (error && 0 == cardsList.size()) {
                                callbacks.onListLoadFail("Error excracting cards.");
                            }
                            else {
                                callbacks.onListLoadSuccess(cardsList);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            callbacks.onListLoadFail(e.getMessage());
                            e.printStackTrace();
                        }
                    });
        }

        @Override
        public void loadList(String tagFilter, ListCallbacks callbacks) {
            loadListEnhanced(
                    null,
                    null,
                    tagFilter,
                    null,
                    null,
                    null,
                    null,
                    null,
                    Config.DEFAULT_CARDS_LOAD_COUNT,
                    callbacks
            );
        }

        @Override
        public void loadListForUser(String userId, ListCallbacks callbacks) {
            loadListEnhanced(
                    null,
                    null,
                    null,
                    Card.KEY_USER_ID,
                    FilterOperator.EQUALS,
                    userId,
                    null,
                    null,
                    null,
                    callbacks
            );
        }

        @Override
        public void loadNewCards(long newerThanTime, ListCallbacks callbacks) {
            loadListEnhanced(
                    null,
                    null,
                    null,
                    Card.KEY_CTIME,
                    FilterOperator.GREATER,
                    newerThanTime,
                    null,
                    null,
                    null,
                    callbacks
            );
        }
    */
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
        throw new RuntimeException("CardsSingleton_CF.updateCommentsCounter() ещё не реализван.");
    }

    @Override
    public String createKey() {
        return cardsCollection.document().getId();
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

        cardReference
                .set(card)
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
        cardsCollection.document(card.getKey()).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callbacks.onCardDeleteSuccess(card);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        callbacks.onCardDeleteError(e.getMessage());
                    }
                });
    }

    @Override
    public void rateUp(String cardId, String byUserId, RatingCallbacks callbacks) {
        throw new RuntimeException("CardsSingleton_CF.rateUp() ещё не реализван.");
    }

    @Override
    public void rateDown(String cardId, String byUserId, RatingCallbacks callbacks) {
        throw new RuntimeException("CardsSingleton_CF.rateDown() ещё не реализван.");
    }


    // Внутренниие методы
    private <T> void loadListEnhanced(
            String orderKey,
            SortOrder sortOrder,

            String withTag,

            String filterKey,
            FilterOperator filterOperator,
            T filterValue,

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

        // Отбор по метке
        if (null != withTag)
            query = query.whereArrayContains(Card.KEY_TAGS, withTag);

        // Начальное значение
        if (null != startAt)
            query = query.startAt(startAt);

        if (null != startAfter)
            query = query.startAfter(startAfter);


        // Конечное значение
        if (null != endAt)
            query = query.endAt(endAt);

        // Предельное количество
        if (null != limit)
            query = query.limit(limit);
        else
            query = query.limit(Config.DEFAULT_CARDS_LOAD_COUNT);


        // Собственно запрос
        query.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<Card> list = new ArrayList<>();
                        boolean error = false;

                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                            try {
                                Card card = documentSnapshot.toObject(Card.class);
                                list.add(card);
                            }
                            catch (Exception e) {
                                error = true;
                                Log.e(TAG, e.getMessage());
                                e.printStackTrace();
                            }
                        }

                        if (error && 0 == list.size()) {
                            callbacks.onListLoadFail("Error exception(s) on cards loading.");
                        }
                        else
                            callbacks.onListLoadSuccess(list);
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
