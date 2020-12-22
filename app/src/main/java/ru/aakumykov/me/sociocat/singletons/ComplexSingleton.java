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
import ru.aakumykov.me.sociocat.models.User;

public class ComplexSingleton {

    private final static String TAG = ComplexSingleton.class.getSimpleName();

    private final FirebaseFirestore mFirebaseFirestore = FirebaseFirestore.getInstance();

    private final iTagsSingleton mTagsSingleton = TagsSingleton.getInstance();
    private final iCardsSingleton mCardsSingleton = CardsSingleton.getInstance();
    private final iUsersSingleton mUsersSingleton = UsersSingleton.getInstance();


    public void deleteTag(@NonNull Tag tag, iComplexSingleton_TagDeletionCallbacks deleteCallbacks) {

        List<String> initialCardsList = tag.getCards();
        List<String> existedCardsList = new ArrayList<>();

        checkCardsExistance(initialCardsList, existedCardsList, new iCheckCardsExistanceCallback() {
            @Override
            public void onComplete() {
                deleteTagWithCheckedListOfCards(tag, existedCardsList, deleteCallbacks);
            }
        });
    }

    public void updateTag(@NonNull Tag oldTag, @NonNull Tag newTag, iComplexSingleton_TagSaveCallbacks saveCallbacks) {

        List<String> initialCardsList = new ArrayList<>(newTag.getCards());
        List<String> existedCardsList = new ArrayList<>();

        checkCardsExistance(initialCardsList, existedCardsList, new iCheckCardsExistanceCallback() {
            @Override
            public void onComplete() {
                updateTagWithCheckedListOfCards(oldTag, newTag, existedCardsList, saveCallbacks);
            }
        });
    }

    private void checkCardsExistance(List<String> initialCardsList, List<String> existingCardsList, iCheckCardsExistanceCallback callback) {

        if (0 == initialCardsList.size()) {
            callback.onComplete();
            return;
        }

        String cardKey = initialCardsList.get(0);
        initialCardsList.remove(0);

        mCardsSingleton.checkCardExists(cardKey, new iCardsSingleton.CardCheckExistingCallbacks() {
            @Override
            public void onCardExists(@NonNull String cardKey) {
                existingCardsList.add(cardKey);
                checkCardsExistance(initialCardsList, existingCardsList, callback);
            }

            @Override
            public void onCardNotExists(@NonNull String notExistingCardKey) {
                checkCardsExistance(initialCardsList, existingCardsList, callback);
            }
        });
    }

    private void deleteTagWithCheckedListOfCards(@NonNull Tag tag, List<String> cardsList, iComplexSingleton_TagDeletionCallbacks callbacks) {

        WriteBatch writeBatch = mFirebaseFirestore.batch();

        CollectionReference cardsCollection = mCardsSingleton.getCardsCollection();
        CollectionReference tagsCollection = mTagsSingleton.getTagsCollection();

        String tagKey = tag.getKey();
        String tagName = tag.getName();
        String ghostTagName = Card.GHOST_TAG_PREFIX + tagName;

        for (String cardKey : cardsList) {
            // Получаю ссылку на карточку
            DocumentReference cardRef = cardsCollection.document(cardKey);

            // Удаляю метку-призрак
            writeBatch.update(cardRef, FieldPath.of(ghostTagName), FieldValue.delete());

            // Удаляю метку из списка меток карточки
            writeBatch.update(cardRef, Card.KEY_TAGS, FieldValue.arrayRemove(tagName));
        }

        // Удаляю метку из коллекции меток
        writeBatch.delete(tagsCollection.document(tagKey));

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

    private void updateTagWithCheckedListOfCards(@NonNull Tag oldTag, @NonNull Tag newTag, List<String> existedCardsList, iComplexSingleton_TagSaveCallbacks saveCallbacks) {

        WriteBatch writeBatch = mFirebaseFirestore.batch();

        CollectionReference tagsCollection = mTagsSingleton.getTagsCollection();
        CollectionReference cardsCollection = mCardsSingleton.getCardsCollection();

        String oldTagKey = oldTag.getKey();
        String oldTagName = oldTag.getName();
        String oldGhostTagName = Card.GHOST_TAG_PREFIX + oldTagName;

        String newTagKey = newTag.getKey();
        String newTagName = newTag.getName();
        String newGhostTagName = Card.GHOST_TAG_PREFIX + newTagName;

        // 1. Метка:
        // Удаляю старую
        writeBatch.delete(tagsCollection.document(oldTagKey));

        // Создаю новую
        writeBatch.set(tagsCollection.document(newTagKey), newTag);

        // 2. Каждая карточка метки:
        for (String cardKey : existedCardsList) {
            DocumentReference cardRef = cardsCollection.document(cardKey);

            // Добавляю новую метку-призрак
            writeBatch.update(cardRef, FieldPath.of(newGhostTagName), true);

            // Удаляю старую метку-призрак
            writeBatch.update(cardRef, FieldPath.of(oldGhostTagName), FieldValue.delete());

            // Добаляю новую метку в список меток карточки
            writeBatch.update(cardRef, Card.KEY_TAGS, FieldValue.arrayUnion(newTagName));

            // Удаляю старую метку из списка меток карточки
            writeBatch.update(cardRef, Card.KEY_TAGS, FieldValue.arrayRemove(oldTagName));
        }

        writeBatch.commit()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        saveCallbacks.onTagSaveSuccess(newTag);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMsg = (null != e.getMessage()) ? e.getMessage() : "Tag saving error";
                        saveCallbacks.onTagSaveError(errorMsg);
                        Log.e(TAG, errorMsg, e);
                    }
                });
    }


    public void deleteCard(@NonNull Card card, iComplexSingleton_CardDeletionCallbacks callbacks) {
        
        String cardKey = card.getKey();
        String userKey = card.getUserId();

        CollectionReference cardsCollection = mCardsSingleton.getCardsCollection();
        CollectionReference usersCollection = mUsersSingleton.getUsersCollection();

        DocumentReference cardRef = cardsCollection.document(cardKey);
        DocumentReference cardAuthorRef = usersCollection.document(userKey);

        WriteBatch writeBatch = mFirebaseFirestore.batch();
        writeBatch.delete(cardRef);
        writeBatch.update(cardAuthorRef, User.KEY_CARDS_KEYS, FieldValue.arrayRemove(cardKey));
        
        writeBatch.commit()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callbacks.onCardDeleteSuccess(card);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMsg = e.getMessage();
                        if (null == errorMsg)
                            errorMsg = "Error removing card: "+card;
                   
                        callbacks.onCardDeleteFailed(errorMsg);
                        Log.e(TAG, errorMsg, e);
                    }
                });
    }


    public interface iComplexSingleton_TagDeletionCallbacks {
        void onTagDeleteSuccess(@NonNull Tag tag);
        void onTagDeleteError(@NonNull String errorMsg);
    }

    public interface iComplexSingleton_TagSaveCallbacks {
        void onTagSaveSuccess(@NonNull Tag tag);
        void onTagSaveError(@NonNull String errorMsg);
    }

    private interface iCheckCardsExistanceCallback {
        void onComplete();
    }

    public interface iComplexSingleton_CardDeletionCallbacks {
        void onCardDeleteSuccess(@NonNull Card card);
        void onCardDeleteFailed(@NonNull String errorMsg);
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
