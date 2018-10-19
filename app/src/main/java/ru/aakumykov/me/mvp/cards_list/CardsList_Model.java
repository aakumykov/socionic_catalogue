package ru.aakumykov.me.mvp.cards_list;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
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

    /* Одиночка: начало */
    private volatile static CardsList_Model ourInstance;
    private CardsList_Model() {}
    static synchronized CardsList_Model getInstance() { if (null == ourInstance) {
            synchronized (CardsList_Model.class) {
                ourInstance = new CardsList_Model();
            }
        }

        return ourInstance;
    }
    /* Одиночка: конецъ */

    private final static String TAG = "CardsList_Model";
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private List<Card> cardsList = new ArrayList<>();
    private iCardsList.Callbacks callbacks;


    @Override
    public void loadList(final iCardsList.Callbacks callbacks, boolean forcePullFromServer) {
        Log.d(TAG, "loadList(forcePullFromServer: "+forcePullFromServer+")");
//        Log.d(TAG, "cardsList: "+cardsList);

        if (forcePullFromServer) {
//            cardsList = new ArrayList<>();
            cardsList.clear();
            Log.d(TAG, "после clear(), cardsList: "+cardsList);
        }

        listRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Card card = dataSnapshot.getValue(Card.class);
                // TODO: проверка корректности Карточки (Card)
                callbacks.onChildAdded(card);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Card card = dataSnapshot.getValue(Card.class);
                callbacks.onChildChanged(card, s);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved(), "+dataSnapshot);
                Card card = dataSnapshot.getValue(Card.class);
                callbacks.onChildRemoved(card);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Card card = dataSnapshot.getValue(Card.class);
                callbacks.onChildMoved(card, s);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callbacks.onCancelled(databaseError.getMessage());
                databaseError.toException().printStackTrace();
            }
        });
    }

}
