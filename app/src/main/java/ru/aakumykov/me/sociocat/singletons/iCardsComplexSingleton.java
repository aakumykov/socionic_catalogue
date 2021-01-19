package ru.aakumykov.me.sociocat.singletons;

import androidx.annotation.NonNull;

import ru.aakumykov.me.sociocat.models.Card;

public interface iCardsComplexSingleton {

    void deleteCardWithTagAndUserChecks(@NonNull Card card, iDeleteCardCallbacks cardCallbacks);

    void updateCardWithUserCheck(@NonNull Card card, @NonNull iUpdateCardCallbacks callbacks);


    interface iCheckConditionCallback {
        void doCheck();
    }

    interface iDeleteCardCallbacks {
        void onCardDeleteSuccess(@NonNull Card card);
        void onCardDeleteError(@NonNull String errorMsg);
    }

    interface iUpdateCardCallbacks {
        void onCardUpdateSuccess(@NonNull Card card);
        void onCardUpdateError(@NonNull String errorMsg);
    }
}
