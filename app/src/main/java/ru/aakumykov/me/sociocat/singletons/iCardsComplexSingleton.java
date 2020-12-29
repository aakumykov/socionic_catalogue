package ru.aakumykov.me.sociocat.singletons;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.WriteBatch;

import ru.aakumykov.me.sociocat.models.Card;

public interface iCardsComplexSingleton {

    void deleteCardWithTagAndUserChecks(@NonNull Card card, iDeleteCardCallbacks cardCallbacks);

    interface iCheckCallbacks {
        void onProduceCheck();
        void onCheckPositiveResult(WriteBatch writeBatch);
        void onCheckNegativeResult(WriteBatch writeBatch);
    }

    interface iCheckConditionCallback {
        void doCheck(iCheckFinishCallback callback);
    }

    interface iCheckFinishCallback {
        void onCheckFinished();
    }

    interface iDeleteCardCallbacks {
        void onCardDeleteSuccess(@NonNull Card card);
        void onCardDeleteError(@NonNull String errorMsg);
    }
}
