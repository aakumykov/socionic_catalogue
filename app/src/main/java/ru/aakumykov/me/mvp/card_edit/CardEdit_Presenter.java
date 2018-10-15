package ru.aakumykov.me.mvp.card_edit;

import android.app.Activity;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.R;
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
        Log.d(TAG, "linkView()");
        if (null == this.view) this.view = view;
    }
    @Override
    public void unlinkView() {
        Log.d(TAG, "unlinkView()");
        this.view = null;
    }


    @Override
    public void onCardRecieved(Card card) {
//        Log.d(TAG, "onCardRecieved(), "+card);

        switch (card.getType()) {
            case Constants.TEXT_CARD:
                view.displayTextCard(card);
                break;
            case Constants.IMAGE_CARD:
                view.displayImageCard(card);
                break;
            default:
                view.showError(R.string.unknown_card_type);
                Log.e(TAG, "Unknown card type: "+card.getType());
        }
    }

    @Override
    public void saveButonClicked() {

    }

    @Override
    public void cancelButtonClicked() {
        view.closeActivity();
    }

    @Override
    public void selectImageButtonClicked() {

    }

    @Override
    public void imageDiscardButtonClicked() {

    }


}