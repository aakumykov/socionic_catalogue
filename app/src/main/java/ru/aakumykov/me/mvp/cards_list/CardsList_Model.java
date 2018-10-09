package ru.aakumykov.me.mvp.cards_list;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.models.Card;

public class CardsList_Model implements iCardsList.Model {

    private final static String TAG = "CardsList_Model";

    /* Одиночка: начало */
    private volatile static CardsList_Model ourInstance;
    static synchronized CardsList_Model getInstance(iCardsList.Callbacks callbacks) {
        if (null == ourInstance) {
            synchronized (CardsList_Model.class) {
                ourInstance = new CardsList_Model(callbacks);
            }
        }
        return ourInstance;
    }
    private CardsList_Model(iCardsList.Callbacks callbacks) {
        Log.d(TAG, "new CardsList_Model()");
        this.callbacks = callbacks;
    }
    /* Одиночка: конецъ */

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private List<Card> cardsList = new ArrayList<>();
    private iCardsList.Callbacks callbacks;

    @Override
    public void loadList() {
        Log.d(TAG, "loadList()");

        DatabaseReference cardsRef = firebaseDatabase.getReference().child(Constants.CARDS_PATH);

        cardsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Card card = ds.getValue(Card.class);
                    if (null != card) {
                        card.setKey(ds.getKey());
                        cardsList.add(card);
                    }
                }
//                Log.d(TAG, "cardsList: "+cardsList);
                callbacks.onLoadSuccess(cardsList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
