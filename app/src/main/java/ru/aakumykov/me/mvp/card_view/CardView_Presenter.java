package ru.aakumykov.me.mvp.card_view;

import android.util.Log;

import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.models.Card;

public class CardView_Presenter implements iCardView.Presenter, iCardView.Callbacks {

    private final static String TAG = "CardView_Presenter";
    private iCardView.View view;
    private iCardView.Model model;

    CardView_Presenter() {
        model = new CardView_Model();
    }

    @Override
    public void cardKeyRecieved(String key) {
//        Log.d(TAG, "cardKeyRecieved("+key+")");
        view.showMessage(R.string.loading_card, Constants.INFO_MSG);
        model.loadCard(key, this);
    }


    @Override
    public void onLoadSuccess(Card card) {

        view.setTitle(card.getTitle());
        view.setDescription(card.getDescription());

        switch (card.getType()) {
            case Constants.TEXT_CARD:
                view.hideMessage();
                view.hideProgressBar();
                view.showQuote();
//                view.displayTextCard(Card card);
                view.setQuote(card.getQuote());
                break;
            case Constants.IMAGE_CARD:
                view.showImage();
//                view.displayImageCard(Card card);
                view.loadImage(card.getImageURL());
                break;
            default:
                view.showMessage(R.string.unknown_card_type, Constants.ERROR_MSG);
                break;
        }
    }

    @Override
    public void onLoadFailed(String msg) {
        view.hideMessage();
        view.hideProgressBar();
        view.showMessage(R.string.card_load_error, Constants.ERROR_MSG);
        // Log.e(TAG, msg); // Это уже выводит модель
    }

    @Override
    public void onLoadCanceled() {
        view.hideMessage();
        view.hideProgressBar();
        view.showMessage(R.string.card_load_canceled, Constants.ERROR_MSG);
    }


    @Override
    public void linkView(iCardView.View view) {
        this.view = view;
    }

    @Override
    public void unlinkView() {
        Log.d(TAG, "unlinkView()");
        this.view = null;
    }

}
