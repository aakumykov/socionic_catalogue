package ru.aakumykov.me.mvp.card_view;

import android.util.Log;

import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.models.Card;

public class CardView_Presenter implements iCardView.Presenter, iCardView.Callbacks {

    private final static String TAG = "CardView_Presenter";
    private iCardView.View view;
    private iCardView.Model model;

    CardView_Presenter() {
        model = new CardView_Model(this);
    }

    public void loadCard(String key) {
        model.loadCard(key);
    }

    @Override
    public void linkView(iCardView.View view) {
        this.view = view;
    }

    @Override
    public void unlinkView() {
        this.view = null;
    }

    @Override
    public void onLoadSuccess(Card card) {
        view.setTitle(card.getTitle());
        view.setQuote(card.getQuote());
        view.setDescription(card.getDescription());
    }

    @Override
    public void onLoadFailed(String msg) {
        view.showError(R.string.card_load_error);
        Log.e(TAG, msg);
    }

    @Override
    public void onLoadCanceled() {
        view.showError(R.string.card_load_canceled);
    }
}
