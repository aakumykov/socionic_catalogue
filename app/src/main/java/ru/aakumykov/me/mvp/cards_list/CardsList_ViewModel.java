package ru.aakumykov.me.mvp.cards_list;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ru.aakumykov.me.mvp.models.Card;

public class CardsList_ViewModel extends ViewModel implements iCardsList.Callbacks {

    private final static String TAG = "CardsList_ViewModel";
    private CardsList_Model model;
    private List<Card> cardsList = new ArrayList<>();
    private MutableLiveData<List<Card>> liveData = new MutableLiveData<>();

    CardsList_ViewModel() {
        Log.d(TAG, "== new CardsList_ViewModel()");
        model = CardsList_Model.getInstance();
    }

    LiveData<List<Card>> getLiveData() {
        return liveData;
    }

    @Override
    public void onLoadSuccess(List<Card> list) {
        Log.d(TAG, "onLoadSuccess(), "+list);
//        List<Card> emptyList = new ArrayList<Card>();
//        liveData.setValue(emptyList);
        liveData.setValue(list);
    }

    @Override
    public void onLoadError() {
        Log.d(TAG, "onLoadError()");
    }

    void loadList() {
        if (cardsList.isEmpty()) {
            Log.d(TAG, "loadList(): список пустой, запрашиваю");
            Log.d(TAG, cardsList.toString());
            model.loadList(this);
        } else {
            Log.d(TAG, "loadList(), список уже заполнен, возвращаю");
            Log.d(TAG, cardsList.toString());
        }
    }
}
