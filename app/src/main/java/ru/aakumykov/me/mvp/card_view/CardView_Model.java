package ru.aakumykov.me.mvp.card_view;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.models.Card;

public class CardView_Model implements iCardView.Model {

    /* Одиночка */
    private static volatile iCardView.Model ourInstance = new CardView_Model();
    static synchronized iCardView.Model getInstance() {
        synchronized (CardView_Model.class) {
            if (null == ourInstance) ourInstance = new CardView_Model();
        }
        return ourInstance;
    }
    private CardView_Model(){}
    /* Одиночка */

    private final static String TAG = "CardView_Model";
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    @Override
    public void loadCard(String key, final iCardView.Callbacks callbacks) {

        firebaseDatabase.getReference().child(Constants.CARDS_PATH).child(key)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Card card = dataSnapshot.getValue(Card.class);
                        if (null != card) {
                            card.setKey(dataSnapshot.getKey());
                            callbacks.onLoadSuccess(card);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        callbacks.onLoadFailed(databaseError.getMessage());
                        databaseError.toException().printStackTrace();
                    }
                });
    }

    @Override
    public void deleteCard(String key, final iCardView.Callbacks callbacks) {
        Log.d(TAG, "deleteCard("+key+")");

        DatabaseReference cardRef = firebaseDatabase.getReference()
                .child(Constants.CARDS_PATH).child(key);

        cardRef.removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                String msg = null;
                if (null != databaseError) {
                    msg = databaseError.getMessage();
                    databaseError.toException().printStackTrace();
                }
                callbacks.onDeleteComplete(msg);
            }
        });
    }
}
