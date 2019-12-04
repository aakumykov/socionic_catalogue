package ru.aakumykov.me.sociocat.singletons;

import androidx.annotation.Nullable;

public interface iRatingSingleton {

    void initRatingCounter(String counterName, String counterOwnerKey, iCreateDistributedCounterCallbacks callbacks);

    interface iCreateDistributedCounterCallbacks {
        void onDistributedCounterCreateSuccess();
        void onDistributedCounterCreateError(String errorMsg);
    }

    void getRating(String counterName, String counterOwnerKey, iGetRatingCallbacks callbacks);

    interface iGetRatingCallbacks {
        void onGetRatingComplete(int value, @Nullable String errorMsg);
    }
}
