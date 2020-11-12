package ru.aakumykov.me.sociocat.singletons;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;

import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.models.Tag;

public class ComplexSingleton {

    private final static String TAG = ComplexSingleton.class.getSimpleName();

    private final iTagsSingleton mTagsSingleton = TagsSingleton.getInstance();
    private final iCardsSingleton mCardsSingleton = CardsSingleton.getInstance();
    private final FirebaseFirestore mFirebaseFirestore = FirebaseFirestore.getInstance();


    public void deleteTag(@NonNull Tag tag, iComplexSingleton_TagDeletionCallbacks callbacks) {

        List<String> initialCardsList = tag.getCards();
        List<String> existingCardsList = new ArrayList<>();

        checkCardsExistance(initialCardsList, existingCardsList, new CheckCardsExistanceCallback() {
            @Override
            public void onComplete() {
                deleteTagWithCheckListOfCards(tag, existingCardsList, callbacks);
            }
        });
    }


    private void checkCardsExistance(List<String> cardsList, List<String> existingCardsList, CheckCardsExistanceCallback callback) {

        if (0 == cardsList.size()) {
            callback.onComplete();
            return;
        }

        String cardKey = cardsList.get(0);
        cardsList.remove(0);

        mCardsSingleton.checkCardExists(cardKey, new iCardsSingleton.CardCheckExistingCallbacks() {
            @Override
            public void onCardExists(@NonNull String cardKey) {
                existingCardsList.add(cardKey);
                checkCardsExistance(cardsList, existingCardsList, callback);
            }

            @Override
            public void onCardNotExists(@NonNull String notExistingCardKey) {
                checkCardsExistance(cardsList, existingCardsList, callback);
            }
        });
    }

    private void deleteTagWithCheckListOfCards(@NonNull Tag tag, List<String> cardsList, iComplexSingleton_TagDeletionCallbacks callbacks) {

        WriteBatch writeBatch = mFirebaseFirestore.batch();

        CollectionReference cardsCollection = mCardsSingleton.getCardsCollection();
        CollectionReference tagsCollection = mTagsSingleton.getTagsCollection();

        for (String cardKey : cardsList) {
            String tagKey = tag.getKey();
            String tagName = tag.getName();
            String ghostTagName = Card.GHOST_TAG_PREFIX + tagName;

            DocumentReference cardRef = cardsCollection.document(cardKey);

            // Удаляю метку-призрак
            writeBatch.update(cardRef, FieldPath.of(ghostTagName), FieldValue.delete());

            // Удаляю метку из списка меток карточки
            writeBatch.update(cardRef, Card.KEY_TAGS, FieldValue.arrayRemove(tagName));

            // Удаляю метку из коллекции меток
            writeBatch.delete(tagsCollection.document(tagKey));
        }

        writeBatch.commit()
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    callbacks.onTagDeleteSuccess(tag);
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    String errorMsg = (null != e.getMessage()) ? e.getMessage() :"Unknown error deleting tag";
                    callbacks.onTagDeleteError(errorMsg);
                    Log.e(TAG, e.getMessage(), e);
                }
            });
    }


    public interface iComplexSingleton_TagDeletionCallbacks {
        void onTagDeleteSuccess(@NonNull Tag tag);
        void onTagDeleteError(@NonNull String errorMsg);
    }

    private interface CheckCardsExistanceCallback {
        void onComplete();
    }


    // Одиночка
    private static volatile ComplexSingleton ourInstance;
    public synchronized static ComplexSingleton getInstance() {
        synchronized (ComplexSingleton.class) {
            if (null == ourInstance) ourInstance = new ComplexSingleton();
            return ourInstance;
        }
    }
    private ComplexSingleton() {
    }
    // Одиночка
}
