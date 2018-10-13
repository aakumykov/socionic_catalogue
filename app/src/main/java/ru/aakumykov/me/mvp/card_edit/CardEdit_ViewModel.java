package ru.aakumykov.me.mvp.card_edit;

import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import ru.aakumykov.me.mvp.models.Card;


public class CardEdit_ViewModel extends android.arch.lifecycle.ViewModel
        implements iCardEdit.ViewModel {

    private final static String TAG = "CardEdit_ViewModel";
    private MutableLiveData<Card> cardLiveData = new MutableLiveData<>();
    private MutableLiveData<String> errorLiveData = new MutableLiveData<>();


    @Override
    public MutableLiveData<Card> getCardLiveData() {
        return cardLiveData;
    }

    @Override
    public MutableLiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

}