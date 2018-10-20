package ru.aakumykov.me.mvp.cards_list;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import ru.aakumykov.me.mvp.models.Card;

public class CardsList_ViewModel extends ViewModel implements
        iCardsList.ViewModel, iCardsList.Callbacks {

    private final static String TAG = "CardsList_ViewModel";
    private CardsList_Model model;
    private MutableLiveData<Card> cardAdd_LiveData = new MutableLiveData<>();
    private MutableLiveData<Card> cardRemove_LiveData = new MutableLiveData<>();
    private MutableLiveData<Card> cardChange_LiveData = new MutableLiveData<>();

    CardsList_ViewModel() {
//        Log.d(TAG, "== new CardsList_ViewModel()");
        model = CardsList_Model.getInstance();
    }

    public MutableLiveData<Card> getCardAdd_LiveData() {
        return cardAdd_LiveData;
    }

    public MutableLiveData<Card> getCardRemove_LiveData() {
        return cardRemove_LiveData;
    }

    public MutableLiveData<Card> getCardChange_LiveData() {
        return cardChange_LiveData;
    }


    // Зачем это здесь, ведь простая обёртка?
    @Override
    public void loadList(boolean forcePullFromServer) {
        model.loadList(this, forcePullFromServer);
    }


    // Методы обратного вызова
    @Override
    public void onChildAdded(Card card) {
//        Log.d(TAG, "childAdded: "+card.getTitle());
        cardAdd_LiveData.setValue(card);
    }

    @Override
    public void onChildChanged(Card card, String previousCardName) {
        Log.d(TAG, "onChildChanged("+card.getTitle()+", "+previousCardName+")");
        cardChange_LiveData.setValue(card);
    }

    @Override
    public void onChildRemoved(Card card) {
        Log.d(TAG, "onChildRemoved(title: "+card.getTitle()+")");
        cardRemove_LiveData.setValue(card);
    }

    @Override
    public void onChildMoved(Card card, String previousCardName) {
        Log.d(TAG, "onChildMoved("+card.getTitle()+", "+previousCardName+")");
    }

    @Override
    public void onCancelled(String errorMessage) {
        Log.e(TAG, "onCancelled("+errorMessage+")");
    }
}
