package ru.aakumykov.me.mvp.card_view;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.models.Card;

public class CardView_Presenter implements iCardView.Presenter, iCardView.Callbacks {

    private final static String TAG = "CardView_Presenter";
    private iCardView.View view;
    private iCardView.Model model;
    private Card currentCard;

    CardView_Presenter() {
        model = CardView_Model.getInstance();
    }

    @Override
    public void cardKeyRecieved(String key) {
//        Log.d(TAG, "onCardRecieved("+key+")");
        view.showMessage(R.string.opening_card, Constants.INFO_MSG);
        model.loadCard(key, this);
    }

    @Override
    public void onEditButtonClicked() {
        Log.d(TAG, "onEditButtonClicked()");
        view.goEditCard(currentCard);
    }


    @Override
    public void onDeleteButtonClicked() {
        Log.d(TAG, "onDeleteButtonClicked()");
        view.showProgressBar();
        view.showMessage(R.string.deleting_card, Constants.INFO_MSG);

        // Ну криво же это!
        try {
            model.deleteCard(currentCard, this);
        } catch (Exception e) {
            view.hideProgressBar();
            view.showMessage(R.string.error_deleting_card, Constants.ERROR_MSG);
        }
    }

    @Override
    public void onDeleteComplete(@Nullable String errorMsg) {
        view.hideProgressBar();

        if (null == errorMsg) {
            view.showMessage(R.string.card_deleted, Constants.INFO_MSG);
            view.close();
        } else {
            view.showMessage(R.string.error_deleting_card, Constants.ERROR_MSG);
        }
    }

    // TODO: удаление также из списка


    @Override
    public void activityResultComes(int requestCode, int resultCode, @Nullable Intent data) {
        if (Activity.RESULT_OK == resultCode) {
            if (null != data) {
                Card card = data.getParcelableExtra(Constants.CARD);
                Log.d(TAG, "card from activity result: "+card);
                if (null != card) {
                    currentCard = card;
                    view.displayCard(card);
                } else {
                    Log.e(TAG, "Card from intent of activity result is null.");
                    view.showMessage(R.string.error_displaying_card, Constants.ERROR_MSG);
                }
            }
        }
    }


    @Override
    public void onLoadSuccess(Card card) {

        this.currentCard = card;

        view.hideMsg();
        view.hideProgressBar();

        view.setTitle(card.getTitle());
        view.setDescription(card.getDescription());

        switch (card.getType()) {
            case Constants.TEXT_CARD:
                view.showQuote();
                view.setQuote(card.getQuote());
//                view.displayTextCard(Card currentCard);
                break;
            case Constants.IMAGE_CARD:
                view.showImagePlaceholder();
                view.loadImage(card.getImageURL());
//                view.displayImageCard(Card currentCard);
                break;
            default:
                view.showMessage(R.string.wrong_card_type, Constants.ERROR_MSG);
                break;
        }
    }

    @Override
    public void onLoadFailed(String msg) {
        this.currentCard = null;

        view.hideMsg();
        view.hideProgressBar();
        view.showMessage(R.string.card_load_error, Constants.ERROR_MSG);
    }

    @Override
    public void onLoadCanceled() {
        // Нужно обнулять currentCard ?
        view.hideMsg();
        view.hideProgressBar();
        view.showMessage(R.string.card_load_canceled, Constants.ERROR_MSG);
    }




    @Override
    public void linkView(iCardView.View view) {
        Log.d(TAG, "linkView(), view: "+view);
        this.view = view;
    }

    @Override
    public void unlinkView() {
        Log.d(TAG, "unlinkView()");
        this.view = null;
    }

}
