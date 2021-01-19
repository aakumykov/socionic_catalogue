package ru.aakumykov.me.sociocat.singletons;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;

import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.models.Tag;
import ru.aakumykov.me.sociocat.models.User;

public class CardsComplexSingleton implements iCardsComplexSingleton {

    private iUsersSingleton mUsersSingleton = UsersSingleton.getInstance();
    private iTagsSingleton mTagsSingleton = TagsSingleton.getInstance();
    private iCardsSingleton mCardsSingleton = CardsSingleton.getInstance();

    @Override
    public void deleteCardWithTagAndUserChecks(@NonNull Card card, iDeleteCardCallbacks cardCallbacks) {

        WriteBatch writeBatch = FirebaseFirestore.getInstance().batch();

        String cardKey = card.getKey();
        String userId = card.getUserId();
        List<String> tagsList = card.getTags();

        List<iCheckConditionCallback> checkConditionsList = new ArrayList<>();

        // Проверка существования пользователя
        checkConditionsList.add(new iCheckConditionCallback() {
            @Override
            public void doCheck() {
                mUsersSingleton.checkUserExists(userId, new iUsersSingleton.iCheckExistanceCallbacks() {
                    @Override
                    public void onCheckComplete() {

                    }

                    @Override
                    public void onExists() {
                        DocumentReference userRef = mUsersSingleton.getUsersCollection().document(userId);
                        writeBatch.update(userRef, User.KEY_CARDS_KEYS, FieldValue.arrayRemove(cardKey));
                    }

                    @Override
                    public void onNotExists() {

                    }

                    @Override
                    public void onCheckFail(String errorMsg) {

                    }
                });
            }
        });

        // Проверка существования меток
        for (String tagName : tagsList) {
            checkConditionsList.add(new iCheckConditionCallback() {
                @Override
                public void doCheck() {
                    mTagsSingleton.checkTagExists(tagName, new iTagsSingleton.ExistanceCallbacks() {
                        @Override
                        public void onTagExists(@NonNull String tagName) {
                            CollectionReference tagsCollection = mTagsSingleton.getTagsCollection();
                            DocumentReference tagRef = tagsCollection.document(tagName);
                            writeBatch.update(tagRef, Tag.KEY_CARDS, FieldValue.arrayRemove(cardKey));
                        }

                        @Override
                        public void onTagNotExists(@Nullable String tagName) {

                        }

                        @Override
                        public void onTagExistsCheckFailed(@NonNull String errorMsg) {

                        }
                    });
                }
            });
        }

        /*CollectionReference cardsCollection = mCardsSingleton.getCardsCollection();
        DocumentReference cardRef = cardsCollection.document(cardKey);
        writeBatch.delete(cardRef).commit()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });*/

        for (iCheckConditionCallback condition : checkConditionsList) {
            condition.doCheck();
        }
    }

    @Override
    public void updateCardWithUserCheck(@NonNull Card card, @NonNull iUpdateCardCallbacks callbacks) {

    }


    // Одиночка
    private static volatile CardsComplexSingleton sInstance;
    public static synchronized CardsComplexSingleton getInstance() {
        if (null == sInstance)
            sInstance = new CardsComplexSingleton();
        return sInstance;
    }
    private CardsComplexSingleton() {}
    // Одиночка
}
