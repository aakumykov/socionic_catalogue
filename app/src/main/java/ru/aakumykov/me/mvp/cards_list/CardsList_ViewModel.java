package ru.aakumykov.me.mvp.cards_list;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ru.aakumykov.me.mvp.models.Card;

public class CardsList_ViewModel extends ViewModel implements
        iCardsList.ViewModel, iCardsList.Callbacks {

    private final static String TAG = "CardsList_ViewModel";
    private CardsList_Model model;
    private List<Card> cardsList = new ArrayList<>();
    private MutableLiveData<List<Card>> liveData = new MutableLiveData<>();

    CardsList_ViewModel() {
//        Log.d(TAG, "== new CardsList_ViewModel()");
        model = CardsList_Model.getInstance();
    }

    public MutableLiveData<List<Card>> getLiveData() {
        return liveData;
    }

    // Зачем это здесь, ведь простая обёртка?
    @Override
    public void loadList(boolean forcePullFromServer) {
        model.loadList(this, forcePullFromServer);
    }


    @Override
    public void onLoadSuccess(List<Card> list) {
//        Log.d(TAG, "onLoadSuccess(), "+list);
        liveData.setValue(list);
    }

    @Override
    public void onLoadError() {
//        Log.d(TAG, "onCardSaveError()");
    }
}
