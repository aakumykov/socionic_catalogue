package ru.aakumykov.me.mvp.card_edit;

import android.arch.lifecycle.MutableLiveData;

import ru.aakumykov.me.mvp.models.Card;


public class CardEdit_ViewModel extends android.arch.lifecycle.ViewModel
        implements iCardEdit.ViewModel, iCardEdit.ModelCallbacks {

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

    @Override
    public void onLoadSuccess(Card card) {
        cardLiveData.setValue(card);
    }

    @Override
    public void onLoadError(String message) {
        errorLiveData.setValue(message);
    }
}