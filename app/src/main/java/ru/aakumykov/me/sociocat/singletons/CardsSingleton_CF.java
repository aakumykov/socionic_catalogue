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
    public void loadList(@Nullable String startKey, @Nullable String endKey, ListCallbacks callbacks) {

        cardsCollection.get()
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

                        if (cardsList.size() > 0 && !cardsErrors)
                            callbacks.onListLoadSuccess(cardsList);
                        else
                            callbacks.onListLoadFail("Error exception(s) on cards loading.");
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
    public void loadCardsWithTag(String tagName, @Nullable String startKey, @Nullable String endKey, ListCallbacks callbacks) {

    }

    @Override
    public void loadList(ListCallbacks callbacks) {

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
    public void loadCard(String key, LoadCallbacks callbacks) {

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
}
