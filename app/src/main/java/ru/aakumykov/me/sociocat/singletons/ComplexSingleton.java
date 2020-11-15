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


    public void deleteTag(@NonNull Tag tag, iComplexSingleton_TagDeletionCallbacks deleteCallbacks) {

        List<String> initialCardsList = tag.getCards();
        List<String> existedCardsList = new ArrayList<>();

        checkCardsExistance(initialCardsList, existedCardsList, new CheckCardsExistanceCallback() {
            @Override
            public void onComplete() {
                deleteTagWithCheckedListOfCards(tag, existedCardsList, deleteCallbacks);
            }
        });
    }

    public void updateTag(@NonNull Tag oldTag, @NonNull Tag newTag, iComplexSingleton_TagSaveCallbacks saveCallbacks) {

        List<String> initialCardsList = newTag.getCards();
        List<String> existedCardsList = new ArrayList<>();

        checkCardsExistance(initialCardsList, existedCardsList, new CheckCardsExistanceCallback() {
            @Override
            public void onComplete() {
                updateTagWithCheckedListOfCards(oldTag, newTag, existedCardsList, saveCallbacks);
            }
        });
    }


    private void checkCardsExistance(List<String> initialCardsList, List<String> existingCardsList, CheckCardsExistanceCallback callback) {

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

    private void updateTagWithCheckedListOfCards(@NonNull Tag oldTag, @NonNull Tag tag, List<String> existedCardsList, iComplexSingleton_TagSaveCallbacks saveCallbacks) {

        WriteBatch writeBatch = mFirebaseFirestore.batch();

        CollectionReference tagsCollection = mTagsSingleton.getTagsCollection();
        CollectionReference cardsCollection = mCardsSingleton.getCardsCollection();

        String tagKey = tag.getKey();
        String oldTagName = oldTag.getName();
        String oldGhostTagName = Card.GHOST_TAG_PREFIX + oldTagName;

        // 1. Метка:
        // удаляю старую
        // создаю новую

        // 2. Каждая карточка метки:
        // Удалить старую метку-призрак
        // Добавить новую метку-призрак
        // Удалить старую метку из списка меток
        // Добавить новую метку в список меток

        /*for (String cardKey : existedCardsList) {
            // Получаю ссылку на карточку
            DocumentReference cardRef = cardsCollection.document(cardKey);

            // Удаляю старую метку-призрак
            writeBatch.update(cardRef, FieldPath.of(ghostTagName), FieldValue.delete());
        }*/
    }


    public interface iComplexSingleton_TagDeletionCallbacks {
        void onTagDeleteSuccess(@NonNull Tag tag);
        void onTagDeleteError(@NonNull String errorMsg);
    }

    public interface iComplexSingleton_TagSaveCallbacks {
        void onTagSaveSuccess(@NonNull Tag tag);
        void onTagSaveError(@NonNull String errorMsg);
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
