package ru.aakumykov.me.mvp.services;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

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
import java.util.List;

import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.interfaces.iCardsSingleton;
import ru.aakumykov.me.mvp.models.Card;

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
    public void updateCard(final Card card, final SaveCardCallbacks callbacks) {
        Log.d(TAG, "updateCard(), "+card);

        DatabaseReference cardRef = cardsRef.child(card.getKey());

        cardRef.setValue(card)
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
        Log.d(TAG, "deleteCardConfigmed(), "+card);

        DatabaseReference cardRef = cardsRef.child(card.getKey());

        cardRef.removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (null == databaseError) {
                    callbacks.onCardDeleteSuccess(card);
                } else {
                    callbacks.onCardDeleteError(databaseError.getMessage());
                    databaseError.toException().printStackTrace();
                }
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
                if (null != dataSnapshot) {
                    Log.e(TAG, "dataSnapshot: "+dataSnapshot);
                }
            }
        });
    }


    @Override
    public void rateUp(String cardId, String byUserId, final RatingCallbacks callbacks) {
        changeRating(cardId, byUserId,1, callbacks);
    }


    @Override
    public void rateDown(String cardId, String byUserId, RatingCallbacks callbacks) {
        changeRating(cardId, byUserId, -1, callbacks);
    }


    @Override
    public void loadList(ListCallbacks callbacks) {
        Log.d(TAG, "loadList()");
        loadList(null, callbacks);
    }


    @Override
    public void loadList(@Nullable String tagFilter, final ListCallbacks callbacks) {
        Log.d(TAG, "loadList(tagFilter: "+ tagFilter +", ...)");

        Query query = (null != tagFilter)
            ? cardsRef.orderByChild("tags/"+tagFilter).equalTo(true)
            : cardsRef.orderByKey();

        // TODO: а где уходить в оффлайн?
        query.addListenerForSingleValueEvent(new ValueEventListener() {
//        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Log.d(TAG, "onDataChange(), "+dataSnapshot);

                List<Card> list = new ArrayList<>();

                for (DataSnapshot snapshotPiece : dataSnapshot.getChildren()) {
                    try {
                        Card card = snapshotPiece.getValue(Card.class);

                        if (null != card) {
                            card.setKey(snapshotPiece.getKey());
                            list.add(card);
                        } else {
                           callbacks.onListLoadFail("Card from snapshotPiece is null");
                           Log.d(TAG, "snapshotPiece: "+snapshotPiece);
                        }

                    } catch (Exception e) {
                        // Здесь бы сообщение пользователю, но оно затрётся инфой
                        Log.e(TAG, e.getMessage()+", snapshotPiece: "+snapshotPiece);
                        e.printStackTrace();
                    }
                }

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
    private void changeRating(final String cardId, final String byUserId, final int ratingDifference, final RatingCallbacks callbacks) {

        DatabaseReference theCardRef = cardsRef.child(cardId);

        theCardRef.runTransaction(new Transaction.Handler() {

            @NonNull
            @Override
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
                        if (ratingDifference > 0) callbacks.onRetedUp(card.getRating());
                        else callbacks.onRatedDown(card.getRating());
                    } else {
                        Log.e(TAG, "Card from dataSnapshot is null");
                    }

                } else {
                    String errorMsg = "Unknown error during rating update of card ("+cardId+").";
                    if (null != databaseError) {
                        errorMsg = databaseError.getMessage();
                        databaseError.toException().printStackTrace();
                    }
                    callbacks.onRateFail(errorMsg);
                }
            }
        });
    }
}