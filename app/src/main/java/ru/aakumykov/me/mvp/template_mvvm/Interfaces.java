package ru.aakumykov.me.mvp.template_mvvm;

import android.arch.lifecycle.MutableLiveData;

import ru.aakumykov.me.mvp.models.Card;

public interface Interfaces {

    interface View {

    }

    interface ViewModel {
        MutableLiveData<Card> getLiveData();
    }

    interface Model {

    }

    interface ModelCallbacks {
        void onLoadSuccess(Card card);
        void onLoadError(String message);
    }
}
