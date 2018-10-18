package ru.aakumykov.me.mvp.card_view;

import android.support.annotation.Nullable;
import android.util.Log;
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


    // Получение карточки
    @Override
    public void cardKeyRecieved(String key) {
        view.showWaitScreen();
        model.loadCard(key, this);
    }

    @Override
    public void onLoadSuccess(Card card) {
        this.currentCard = card;
        view.displayCard(card);
    }

    @Override
    public void onLoadFailed(String msg) {
        this.currentCard = null;
        view.showErrorMsg(R.string.card_load_error);
    }

    @Override
    public void onLoadCanceled() {
        view.showErrorMsg(R.string.card_load_canceled);
    }


    // Реакция на кнопки
    @Override
    public void onEditButtonClicked() {
        view.goEditPage(currentCard);
    }

    // TODO: удаление также и из списка
    @Override
    public void onDeleteButtonClicked() {
        Log.d(TAG, "onDeleteButtonClicked()");
        view.showDeleteDialog();
    }

    @Override
    public void onDeleteConfirmed() {
        view.showProgressMessage(R.string.deleting_card);
        try {
            model.deleteCard(currentCard, this);
        } catch (Exception e) {
            view.hideProgressMessage();
            view.showErrorMsg(R.string.error_deleting_card);
        }
    }

    @Override
    public void onDeleteComplete(@Nullable String errorMsg) {
        view.hideProgressMessage();

        if (null == errorMsg) {
            view.closePage();
        } else {
            view.showErrorMsg(R.string.error_deleting_card);
        }
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
