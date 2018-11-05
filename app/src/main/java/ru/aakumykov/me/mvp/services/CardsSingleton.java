package ru.aakumykov.me.mvp.services;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

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
    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

    private DatabaseReference cardsRef = firebaseDatabase.getReference().child(Constants.CARDS_PATH);
    private StorageReference imagesRef = firebaseStorage.getReference().child(Constants.IMAGES_PATH);

    private UploadTask uploadTask;


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
                callbacks.onLoadSuccess(card);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callbacks.onLoadFailed(databaseError.getMessage());
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
        Log.d(TAG, "deleteCard(), "+card);

        DatabaseReference cardRef = cardsRef.child(card.getKey());

        cardRef.removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (null == databaseError) {
                    callbacks.onDeleteSuccess(card);
                } else {
                    callbacks.onDeleteError(databaseError.getMessage());
                    databaseError.toException().printStackTrace();
                }
            }
        });
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
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
        query.addValueEventListener(new ValueEventListener() {
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

}
