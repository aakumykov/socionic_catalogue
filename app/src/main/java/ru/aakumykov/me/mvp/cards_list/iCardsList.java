package ru.aakumykov.me.mvp.cards_list;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import java.util.List;

import ru.aakumykov.me.mvp.models.Card;

public interface iCardsList {

    interface View {

    }

    interface ViewModel {
        MutableLiveData<List<Card>> getLiveData();
        void loadList(boolean forcePullFromServer);
    }

    interface Model {
        void loadList(iCardsList.Callbacks callbacks, boolean fromServer);
    }

    interface Callbacks {
        void onLoadSuccess(List<Card> list);
        void onLoadError();
    }
}
