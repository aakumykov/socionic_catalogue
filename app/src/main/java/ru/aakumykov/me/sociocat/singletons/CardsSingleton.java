package ru.aakumykov.me.sociocat.singletons;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import ru.aakumykov.me.sociocat.Config;
import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.models.Tag;
import ru.aakumykov.me.sociocat.models.User;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class CardsSingleton implements iCardsSingleton {

    private final static String TAG = "CardsSingleton";
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference cardsCollection = firebaseFirestore.collection(Constants.CARDS_PATH);
    private CollectionReference tagsCollection = firebaseFirestore.collection(Constants.TAGS_PATH);
    private CollectionReference usersCollection = firebaseFirestore.collection(Constants.USERS_PATH);

    // Шаблона Одиночки начало
    private static volatile CardsSingleton ourInstance;
    public synchronized static CardsSingleton getInstance() {
        synchronized (CardsSingleton.class) {
            if (null == ourInstance) ourInstance = new CardsSingleton();
            return ourInstance;
        }
    }
    private CardsSingleton() { }
    // Шаблона Одиночки конец


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
                cardToLoadAfter,
                null,
                null,
                callbacks
        );
    }

    @Override
    public void loadCardsWithTag(String tagName, ListCallbacks callbacks) {

        Query query = cardsCollection.whereEqualTo(Card.GHOST_TAG_PREFIX+tagName, true);

        query.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        extractCardsFromQuerySnapshot(queryDocumentSnapshots, new iExtractQuerySnapshotCallbacks() {
                            @Override
                            public void OnExtractSuccess(List<Card> cardsList) {
                                callbacks.onListLoadSuccess(cardsList);
                            }
                            @Override
                            public void OnExtractFail(List<String> errorsList) {
                                callbacks.onListLoadFail("Error exception(s) on cards loading.");
                                Log.e(TAG, TextUtils.join("\n", errorsList));
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callbacks.onListLoadFail(e.getMessage());
                        Log.e(TAG, Arrays.toString(e.getStackTrace()));
                    }
                });
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
                cardToLoadAfter,
                null,
                null,
                callbacks
        );
    }

    @Override
    public void loadCardsFromNewestTo(Card endAtCard, ListCallbacks callbacks) {
        loadListEnhanced(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                endAtCard,
                null,
                callbacks
        );
    }

    @Override
    public void loadCardsWithTagFromNewestTo(String tag, Card endAtCard, ListCallbacks callbacks) {
        loadListEnhanced(
                null,
                null,
                tag,
                null,
                null,
                null,
                null,
                endAtCard,
                null,
                callbacks
        );
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
        throw new RuntimeException("CardsSingleton.updateCommentsCounter() ещё не реализван.");
    }

    @Override
    public String createKey() {
        return cardsCollection.document().getId();
    }

    @Override
    public void saveCard(Card card, @Nullable Card oldCard, SaveCardCallbacks callbacks) {

        DocumentReference cardReference;

        if (null == card.getKey()) {
            cardReference = cardsCollection.document();
            card.setKey(cardReference.getId());
        }
        else {
            cardReference = cardsCollection.document(card.getKey());
        }


        WriteBatch writeBatch = firebaseFirestore.batch();

        Map<String,Object> cardAsMap = card.toMap();

        // В коллекцию "карточки"
        writeBatch.set(cardReference, cardAsMap);

        // Пользователю
        String userId = UsersSingleton.getInstance().getCurrentUser().getKey();
        DocumentReference cardAuthorRef = usersCollection.document(userId);
        writeBatch.update(cardAuthorRef, User.KEY_CARDS_KEYS, FieldValue.arrayUnion(card.getKey()));

        // В метки
        List<String> newTagNames = card.getTags();
        List<String> oldTagNames = (null != oldCard) ? oldCard.getTags() : new ArrayList<>();

        List<String> addedTagNames = MyUtils.listDiff(newTagNames, oldTagNames);
        List<String> removedTagNames = MyUtils.listDiff(oldTagNames, newTagNames);

        for (String tagName : addedTagNames) {
            // Создаю объект Tag в коллекции меток (а автоматом? нет, автоматом не добавляется)
            writeBatch.set(tagsCollection.document(tagName), new Tag(tagName));
            // TODO: KEY_CARDS --> KEY_CARDS
            writeBatch.update(tagsCollection.document(tagName), Tag.KEY_CARDS, FieldValue.arrayUnion(card.getKey()));
        }

        for (String tagName : removedTagNames)
            writeBatch.update(tagsCollection.document(tagName), Tag.KEY_CARDS, FieldValue.arrayRemove(card.getKey()));

        writeBatch.commit()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callbacks.onCardSaveSuccess(card);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callbacks.onCardSaveError(e.getMessage());
                        Log.e(TAG, Arrays.toString(e.getStackTrace()));
                    }
                });
    }

    @Override
    public void deleteCard(Card card, DeleteCallbacks callbacks) {

        String cardKey = card.getKey();

        WriteBatch writeBatch = firebaseFirestore.batch();

        writeBatch.delete(cardsCollection.document(cardKey));

        writeBatch.update(usersCollection.document(card.getUserId()), User.KEY_CARDS_KEYS, FieldValue.arrayRemove(cardKey));

        for (String tagName : card.getTags())
            writeBatch.update(tagsCollection.document(tagName), Tag.KEY_CARDS, FieldValue.arrayRemove(cardKey));

        writeBatch.commit()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callbacks.onCardDeleteSuccess(card);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callbacks.onCardDeleteError(e.getMessage());
                        Log.e(TAG, Arrays.toString(e.getStackTrace()));
                    }
                });
    }

    @Override
    public void rateUp(String cardId, String byUserId, RatingCallbacks callbacks) {
        throw new RuntimeException("CardsSingleton.rateUp() ещё не реализван.");
    }

    @Override
    public void rateDown(String cardId, String byUserId, RatingCallbacks callbacks) {
        throw new RuntimeException("CardsSingleton.rateDown() ещё не реализван.");
    }


    // Внутренниие методы
    private <T> void loadListEnhanced(
            String orderKey,
            SortOrder sortOrder,

            String withTag,

            String filterKey,
            FilterOperator filterOperator,
            T filterValue,

            Card startAfterCard,
            Card endAtCard,

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
        if (null != startAfterCard)
            query = query.startAfter(startAfterCard.getCTime());

        // Конечное значение
        if (null != endAtCard)
            query = query.endAt(endAtCard.getCTime());

        // Предельное количество
        if (null != limit)
            query = query.limit(limit);
        else {
            /* Если установлена нижняя граница через карточку,
               количество по умолчанию ставить не нужно. */
            if (null == endAtCard)
                query = query.limit(Config.DEFAULT_CARDS_LOAD_COUNT);
        }


        // Собственно запрос
        query.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        extractCardsFromQuerySnapshot(queryDocumentSnapshots, new iExtractQuerySnapshotCallbacks() {
                            @Override
                            public void OnExtractSuccess(List<Card> cardsList) {
                                callbacks.onListLoadSuccess(cardsList);
                            }
                            @Override
                            public void OnExtractFail(List<String> errorsList) {
                                callbacks.onListLoadFail("Error exception(s) on cards loading.");
                                Log.e(TAG, TextUtils.join("\n", errorsList));
                            }
                        });
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

    private void extractCardsFromQuerySnapshot(QuerySnapshot queryDocumentSnapshots, iExtractQuerySnapshotCallbacks callbacks) {
        List<Card> cardsList = new ArrayList<>();
        List<String> errorsList = new ArrayList<>();

        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
            try {
                Card card = documentSnapshot.toObject(Card.class);
                cardsList.add(card);
            }
            catch (Exception e) {
                String errorText = e.getMessage();
                errorText += Arrays.toString(e.getStackTrace());
                errorsList.add(errorText);
            }
        }

        if (errorsList.size() > 0 && 0 == cardsList.size())
            callbacks.OnExtractFail(errorsList);
        else
            callbacks.OnExtractSuccess(cardsList);
    }

    private interface iExtractQuerySnapshotCallbacks {
        void OnExtractSuccess(List<Card> cardsList);
        void OnExtractFail(List<String> errorsList);
    }
}
