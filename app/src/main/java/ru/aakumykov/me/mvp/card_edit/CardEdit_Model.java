package ru.aakumykov.me.mvp.card_edit;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.models.Card;

public class CardEdit_Model implements iCardEdit.Model {

    /* Одиночка */
    private static volatile CardEdit_Model ourInstance = new CardEdit_Model();
    private CardEdit_Model() { }
    public static synchronized CardEdit_Model getInstance() {
        synchronized (CardEdit_Model.class) {
            if (null == ourInstance) {
                ourInstance = new CardEdit_Model();
            }
            return ourInstance;
        }
    }
    /* Одиночка */

    private final static String TAG = "CardEdit_Model";
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    @Override
    public void saveCard(Card card, iCardEdit.ModelCallbacks callbacks) {

    }

    //    @Override
//    public void loadCard(String key, final iCardEdit.ModelCallbacks callbacks) {
//        DatabaseReference cardRef = firebaseDatabase.getReference()
//                .child(Constants.CARDS_PATH).child(key);
//
//        cardRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Card card = dataSnapshot.getValue(Card.class);
//                if (null != card) {
//                    card.setKey(dataSnapshot.getKey());
//                    callbacks.onLoadSuccess(card);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                callbacks.onLoadError(databaseError.getMessage());
//                databaseError.toException().printStackTrace();
//            }
//        });
//    }
}
