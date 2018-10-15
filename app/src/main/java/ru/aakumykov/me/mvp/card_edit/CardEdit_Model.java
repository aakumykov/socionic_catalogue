package ru.aakumykov.me.mvp.card_edit;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
    public void saveCard(Card card, final iCardEdit.ModelCallbacks callbacks) {
        Log.d(TAG, "saveCard(), "+card);

        DatabaseReference cardRef = firebaseDatabase.getReference()
                .child(Constants.CARDS_PATH).child(card.getKey());

        cardRef.setValue(card)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callbacks.onCardSaveSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callbacks.onCardSaveError(e.getMessage());
                        e.printStackTrace();
                    }
                })
                .addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        callbacks.onCardSaveCancel();
                    }
                });
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
//                    callbacks.onCardSaveSuccess(card);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                callbacks.onCardSaveError(databaseError.getMessage());
//                databaseError.toException().printStackTrace();
//            }
//        });
//    }
}
