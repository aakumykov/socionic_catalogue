package ru.aakumykov.me.sociocat.singletons;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.aakumykov.me.sociocat.Config;
import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.models.Card;

public class CardsSingleton implements
        iCardsSingleton
{
    /* Одиночка */
    private static volatile CardsSingleton ourInstance;
    public synchronized static CardsSingleton getInstance() {
        synchronized (CardsSingleton.class) {
            if (null == ourInstance) ourInstance = new CardsSingleton();
            return ourInstance;
        }
    }
    private CardsSingleton() {}
    /* Одиночка */

    // Свойства
    private final static String TAG = "CardsSingleton";
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference rootRef = firebaseDatabase.getReference("/");
    private DatabaseReference cardsRef = firebaseDatabase.getReference().child(Constants.CARDS_PATH);

    // Интерфейсные методы
    @Override
    public String createKey() {
        return cardsRef.push().getKey();
    }


    @Override
    public void loadCard(String key, final LoadCallbacks callbacks) {
        Log.d(TAG, "loadCard("+key+")");

        DatabaseReference cardRef = cardsRef.child(key);

        cardRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Object obj = dataSnapshot.getValue();
                Card card = dataSnapshot.getValue(Card.class);
                callbacks.onCardLoadSuccess(card);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callbacks.onCardLoadFailed(databaseError.getMessage());
                databaseError.toException().printStackTrace();
            }
        });
    }


    @Override
    public void saveCard(final Card card, final SaveCardCallbacks callbacks) {

        if (null == card.getKey())
            card.setKey(createKey());

        Map<String,Object> updatePool = new HashMap<>();

        updatePool.put(Constants.CARDS_PATH+"/"+card.getKey(), card);

        updatePool.put(Constants.USERS_PATH+"/"+card.getUserId()+"/cardsKeys/"+card.getKey(), true);

        rootRef.updateChildren(updatePool)
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
                        e.printStackTrace();
                    }
                });
    }


    @Override
    public void deleteCard(final Card card, final  DeleteCallbacks callbacks) {

        String cardKey = card.getKey();
        String userId = card.getUserId();

        Map<String,Object> updatePool = new HashMap<>();

        updatePool.put(Constants.CARDS_PATH+"/"+cardKey, null);
        updatePool.put(Constants.USERS_PATH+"/"+userId+"/cardsKeys/"+cardKey, null);

        rootRef.updateChildren(updatePool)
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
                        e.printStackTrace();
                    }
                });
    }


    @Override
    public void updateCommentsCounter(String cardId, final int diffValue) {

        DatabaseReference thisCardRef = cardsRef.child(cardId);

        thisCardRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                Card card = mutableData.getValue(Card.class);

                if (null == card) {
                    return Transaction.success(mutableData);
                }

                card.setCommentsCount(card.getCommentsCount()+diffValue);

                mutableData.setValue(card);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                if (null != databaseError) {
                    Log.e(TAG, databaseError.getMessage());
                    databaseError.toException().printStackTrace();
                }
//                if (null != dataSnapshot) {
//                    Log.e(TAG, "dataSnapshot: "+dataSnapshot);
//                }
            }
        });
    }


    @Override
    public void rateUp(String cardId, String byUserId, final RatingCallbacks callbacks) {
        changeRating(cardId, byUserId, +1, callbacks);
    }


    @Override
    public void rateDown(String cardId, String byUserId, RatingCallbacks callbacks) {
        changeRating(cardId, byUserId, -1, callbacks);
    }


    @Override
    public void loadList(
            @Nullable String startKey,
            @Nullable String endKey,
            Order order,
            ListCallbacks callbacks
    )
    {
        Query query = cardsRef;

        if (Order.ORDER_REVERSED.equals(order)) {
            query = query.orderByChild(Constants.CARD_KEY_CTIME_INVERSED);

            if (null != startKey)
                query = query.startAt(Long.valueOf(startKey));

            if (null != endKey)
                query = query.endAt(Long.valueOf(endKey));
        }
        else {
            query = query.orderByKey();

            if (null != startKey)
                query = query.startAt(startKey);

            if (null != endKey)
                query = query.endAt(endKey);
        }

//        query = query.limitToFirst(Config.DEFAULT_CARDS_LOAD_COUNT);
        query = query.limitToFirst(3);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            //        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Card> list = extractCardsFromSnapshot(dataSnapshot);
                callbacks.onListLoadSuccess(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callbacks.onListLoadFail(databaseError.getMessage());
                databaseError.toException().printStackTrace();
            }
        });
    }

    @Override
    public void loadCardsWithTag(String tagName, @Nullable String startKey, @Nullable String endKey, ListCallbacks callbacks) {
        Query query = cardsRef.orderByChild(Constants.TAGS_PATH + "/" + tagName).equalTo(true);

        if (null != startKey)
            query = query.startAt(startKey);

        if (null != endKey)
            query = query.endAt(endKey);

        query = query.limitToFirst(Config.DEFAULT_CARDS_LOAD_COUNT);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Card> list = extractCardsFromSnapshot(dataSnapshot);
                callbacks.onListLoadSuccess(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callbacks.onListLoadFail(databaseError.getMessage());
                databaseError.toException().printStackTrace();
            }
        });
    }

    @Override
    public void loadList(ListCallbacks callbacks) {
        Log.d(TAG, "loadList()");
        loadList(null, callbacks);
    }

    @Override
    public void loadList(int limit, ListCallbacks callbacks) {

        Query query = cardsRef.orderByKey().limitToFirst(limit);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Card> list = extractCardsFromSnapshot(dataSnapshot);
                callbacks.onListLoadSuccess(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callbacks.onListLoadFail(databaseError.getMessage());
                databaseError.toException().printStackTrace();
            }
        });
    }

    @Override
    public void loadList(@Nullable String tagName, final ListCallbacks callbacks) {
//        Log.d(TAG, "loadList(tagName: "+ tagName +", ...)");

        Query query = (null != tagName)
            ? cardsRef.orderByChild("tags/"+tagName).equalTo(true)
            : cardsRef.orderByKey();

        // TODO: а где уходить в оффлайн?
        query.addListenerForSingleValueEvent(new ValueEventListener() {
//        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Card> list = extractCardsFromSnapshot(dataSnapshot);
                callbacks.onListLoadSuccess(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callbacks.onListLoadFail(databaseError.getMessage());
                databaseError.toException().printStackTrace();
            }
        });
    }

    @Override
    public void loadListForUser(String userId, ListCallbacks callbacks) {

        Query query = cardsRef.orderByChild("userId").equalTo(userId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Card> list = extractCardsFromSnapshot(dataSnapshot);
                callbacks.onListLoadSuccess(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callbacks.onListLoadFail(databaseError.getMessage());
                databaseError.toException().printStackTrace();
            }
        });
    }

    @Override
    public void loadNewCards(long newerThanTime, final ListCallbacks callbacks) {

        Query query = cardsRef.orderByChild("ctime").startAt(newerThanTime);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Card> list = extractCardsFromSnapshot(dataSnapshot);
                callbacks.onListLoadSuccess(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callbacks.onListLoadFail(databaseError.getMessage());
                databaseError.toException().printStackTrace();
            }
        });

    }


    // Внутренние методы
    private void changeRating(
            final String cardId,
            final String byUserId,
            final int ratingDifference,
            final RatingCallbacks callbacks
    ) {
        DatabaseReference theCardRef = cardsRef.child(cardId);

        theCardRef.runTransaction(new Transaction.Handler() {

            @NonNull @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                Card card = mutableData.getValue(Card.class);

                if (null == card) return Transaction.success(mutableData);

                if (ratingDifference > 0) card.rateUp(byUserId);
                else card.rateDown(byUserId);

                mutableData.setValue(card);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

                if (null == databaseError && null != dataSnapshot) {

                    Card card = dataSnapshot.getValue(Card.class);

                    if (null != card) {
                        if (ratingDifference > 0) callbacks.onRatedUp(card, card.getRating());
                        else callbacks.onRatedDown(card, card.getRating());
                    } else {
                        Log.e(TAG, "Card from dataSnapshot is null");
                    }

                } else {
                    String errorMsg = "Unknown error during rating update of card_edit ("+cardId+").";
                    if (null != databaseError) {
                        errorMsg = databaseError.getMessage();
                        databaseError.toException().printStackTrace();
                    }
                    callbacks.onRateFail(errorMsg);
                }
            }
        });
    }

    private List<Card> extractCardsFromSnapshot(DataSnapshot dataSnapshot) {
        List<Card> list = new ArrayList<>();

        for (DataSnapshot snapshotPiece : dataSnapshot.getChildren()) {
            try {
                Card card = snapshotPiece.getValue(Card.class);

                if (null != card) {
                    card.setKey(snapshotPiece.getKey());
                    list.add(card);
                } else {
                    Log.e(TAG, "Card from snapshotPiece is null, snapshotPiece: "+snapshotPiece);
                }

            } catch (Exception e) {
                // Здесь бы сообщение пользователю, но оно затрётся инфой
                Log.e(TAG, e.getMessage()+", snapshotPiece: "+snapshotPiece);
                e.printStackTrace();
            }
        }

        return list;
    }
}
