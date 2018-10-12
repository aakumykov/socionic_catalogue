package ru.aakumykov.me.mvp.cards_list;

import android.arch.lifecycle.MutableLiveData;

import java.util.List;

import ru.aakumykov.me.mvp.models.Card;

public interface iCardsList {

    interface View {

    }

    interface ViewModel {
        MutableLiveData<List<Card>> getLiveData();
    }

    interface Model {
        void loadList(iCardsList.Callbacks callbacks);
    }

    interface Callbacks {
        void onLoadSuccess(List<Card> list);
        void onLoadError();
    }
}
