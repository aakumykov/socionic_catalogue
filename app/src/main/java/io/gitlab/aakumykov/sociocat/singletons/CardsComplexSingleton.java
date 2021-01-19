package io.gitlab.aakumykov.sociocat.singletons;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.gitlab.aakumykov.sociocat.constants.Constants;
import io.gitlab.aakumykov.sociocat.models.Card;
import io.gitlab.aakumykov.sociocat.models.User;
import io.gitlab.aakumykov.sociocat.utils.SleepingThread;

public class CardsComplexSingleton implements iCardsComplexSingleton {

    private iUsersSingleton mUsersSingleton = UsersSingleton.getInstance();
    private iTagsSingleton mTagsSingleton = TagsSingleton.getInstance();
    private iCardsSingleton mCardsSingleton = CardsSingleton.getInstance();


    @Override
    public void deleteCardWithChecks(@Nullable Card card, iDeleteCardCallbacks callbacks) {

        if (null == card) {
            callbacks.onCardDeleteFailed("Card cannot be null");
            return;
        }

        List<Runnable> checksList = new ArrayList<>();
        Map<String,Boolean> checksMap = new HashMap<>();

        String cardKey = card.getKey();
        String userKey = card.getUserId();

        DocumentReference cardRef = mCardsSingleton.getCardsCollection().document(cardKey);
        DocumentReference cardAuthorRef = mUsersSingleton.getUsersCollection().document(userKey);

        WriteBatch writeBatch = FirebaseFirestore.getInstance().batch();
        writeBatch.delete(cardRef);

        // Проверка существования пользователя
        checksList.add(new Runnable() {
            @Override
            public void run() {
                String userId = card.getUserId();
                mUsersSingleton.checkUserExists(userId, new iUsersSingleton.iCheckExistanceCallbacks() {
                    @Override
                    public void onCheckComplete() {

                    }

                    @Override
                    public void onExists() {
                        synchronized (checksMap) {
                            checksMap.put(userId, true);
                            writeBatch.update(cardAuthorRef,
                                    User.KEY_CARDS_KEYS, FieldValue.arrayRemove(cardKey));
                        }
                    }

                    @Override
                    public void onNotExists() {
                        synchronized (checksMap) {
                            checksMap.put(userId, false);
                        }
                    }

                    @Override
                    public void onCheckFail(String errorMsg) {
                        // TODO: сделать адекватную реакцию
                    }
                });
            }
        });

        // Проверка существования меток
        CollectionReference tagsCollection = mTagsSingleton.getTagsCollection();

        for (String tagName : card.getTags()) {
            checksList.add(new Runnable() {
                @Override
                public void run() {
                    mTagsSingleton.checkTagExists(tagName, new iTagsSingleton.ExistanceCallbacks() {
                        @Override
                        public void onTagExists(@NonNull String tagName) {
                            synchronized (checksMap) {
                                checksMap.put(tagName, true);
                                writeBatch.update(
                                        tagsCollection.document(tagName),
                                        Constants.CARDS_IN_TAG_PATH,
                                        FieldValue.arrayRemove(card.getKey())
                                );
                            }
                        }

                        @Override
                        public void onTagNotExists(@Nullable String tagName) {
                            synchronized (checksMap) {
                                checksMap.put(tagName, false);
                            }
                        }

                        @Override
                        public void onTagExistsCheckFailed(@NonNull String errorMsg) {

                        }
                    });
                }
            });
        }

        // Ожидание завершения проверок
        new SleepingThread(
                30,
                new SleepingThread.iSleepingThreadCallbacks() {
                    @Override
                    public void onSleepingStart() {

                    }

                    @Override
                    public void onSleepingTick(int secondsToWakeUp) {

                    }

                    @Override
                    public void onSleepingEnd() {
                        executeCardDeletion(card, writeBatch, callbacks);
                    }

                    @Override
                    public boolean isReadyToWakeUpNow() {
                        List<Boolean> checkResults = new ArrayList<>(checksMap.values());
                        return checkResults.size() == checksList.size();
                    }

                    @Override
                    public void onSleepingError(@NonNull String errorMsg) {
                        callbacks.onCardDeleteFailed(errorMsg);
                    }
                }
        ).start();

        // Запуск проверок на исполнение
        for (Runnable aCheck : checksList)
            aCheck.run();
    }


    @Override
    public void updateCardWithUserCheck(@NonNull Card card, @NonNull iUpdateCardCallbacks callbacks) {

    }


    private void executeCardDeletion(@NonNull Card card, @NonNull WriteBatch writeBatch, @NonNull iDeleteCardCallbacks callbacks) {
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
                            errorMsg = "Unknown error";
                        callbacks.onCardDeleteFailed(errorMsg);
                        e.printStackTrace();
                    }
                });
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
