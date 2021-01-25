package ru.aakumykov.me.sociocat.singletons;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.models.Card;

public interface iCardsComplexSingleton {

    void deleteCardWithChecks(@Nullable Card card, iDeleteCardCallbacks callbacks);

    void updateCardWithUserCheck(@NonNull Card card, @NonNull iUpdateCardCallbacks callbacks);


    interface iDeleteCardCallbacks {
        void onCardDeleteSuccess(@NonNull Card card);
        void onCardDeleteFailed(@NonNull String errorMsg);
    }

    interface iUpdateCardCallbacks {
        void onCardUpdateSuccess(@NonNull Card card);
        void onCardUpdateError(@NonNull String errorMsg);
    }
}
