package ru.aakumykov.me.sociocat.singletons;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import ru.aakumykov.me.sociocat.models.Tag;

public class ComplexSingleton {

    private final static String TAG = ComplexSingleton.class.getSimpleName();
    private final iTagsSingleton mTagsSingleton = TagsSingleton.getInstance();
    private final iCardsSingleton mCardsSingleton = CardsSingleton.getInstance();


    public interface iComplexSingleton_TagDeletionCallbacks {
        void onTagDeleteSuccess(@NonNull Tag tag);
        void onTagDeleteError(@NonNull String errorMsg);
    }


    public void deleteTag(@NonNull Tag tag, iComplexSingleton_TagDeletionCallbacks callbacks) {

        List<String> initialCardsList = tag.getCards();
        List<String> existingCardsList = new ArrayList<>();

        checkCardsExistance(initialCardsList, existingCardsList);
    }

    private void checkCardsExistance(List<String> cardsList, List<String> existingCardsList) {

        if (0 == cardsList.size()) {
            processExistingCardsList(existingCardsList);
            return;
        }

        String cardKey = cardsList.get(0);
        cardsList.remove(0);

        mCardsSingleton.checkCardExists(cardKey, new iCardsSingleton.CardCheckExistingCallbacks() {
            @Override
            public void onCardExists(@NonNull String cardKey) {
                existingCardsList.add(cardKey);
                checkCardsExistance(cardsList, existingCardsList);
            }

            @Override
            public void onCardNotExists(@NonNull String notExistingCardKey) {
                checkCardsExistance(cardsList, existingCardsList);
            }
        });
    }

    private void processExistingCardsList(List<String> existingCardsList) {
        Log.i(TAG, existingCardsList.toString());
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
