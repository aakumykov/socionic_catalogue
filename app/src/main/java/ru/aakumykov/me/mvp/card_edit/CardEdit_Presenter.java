package ru.aakumykov.me.mvp.card_edit;

import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import ru.aakumykov.me.mvp.models.Card;


public class CardEdit_Presenter extends android.arch.lifecycle.ViewModel
        implements iCardEdit.Presenter {

    private final static String TAG = "CardEdit_Presenter";
    private iCardEdit.View view;
    private iCardEdit.Model model;

    CardEdit_Presenter() {
        Log.d(TAG, "new CardEdit_Presenter()");
        if (null == model) model = CardEdit_Model.getInstance();
    }

    @Override
    public void linkView(iCardEdit.View view) {
        this.view = view;
    }
    @Override
    public void unlinkView() {
        this.view = null;
    }


    @Override
    public void cardRecieved(Card card) {
        Log.d(TAG, "cardRecieved(), "+card);
    }

    @Override
    public void saveButonClicked() {

    }

    @Override
    public void cancelButtonClicked() {

    }

    @Override
    public void selectImageButtonClicked() {

    }

    @Override
    public void imageDiscardButtonClicked() {

    }
}