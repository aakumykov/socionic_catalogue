package ru.aakumykov.me.mvp.cards_list;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import java.util.List;

import ru.aakumykov.me.mvp.models.Card;

public interface iCardsList {

    interface View {
        void onAddCardButton(String cardType);
    }

    interface ViewModel {
        MutableLiveData<Card> getCardAdd_LiveData();
        void loadList(boolean forcePullFromServer);
    }

    interface Model {
        void loadList(iCardsList.Callbacks callbacks, boolean fromServer);
    }

    interface Callbacks {
//        void onLoadSuccess(List<Card> list);
//        void onLoadError();
        void onChildAdded(Card card);
        void onChildChanged(Card card, String previousCardName); // или title?
        void onChildRemoved(Card card);
        void onChildMoved(Card card, String previousCardName); // или title?
        void onCancelled(String errorMessage);
    }
}
