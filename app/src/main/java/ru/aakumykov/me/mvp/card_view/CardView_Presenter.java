package ru.aakumykov.me.mvp.card_view;

import android.support.annotation.Nullable;
import android.util.Log;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.interfaces.MyInterfaces;
import ru.aakumykov.me.mvp.models.Card;

public class CardView_Presenter implements
        iCardView.Presenter,
        MyInterfaces.CardsService.CardCallbacks
{

    private final static String TAG = "CardView_Presenter";
    private iCardView.View view;
    private MyInterfaces.CardsService model;
    private Card currentCard;

    CardView_Presenter() {}


    // Получение карточки
    @Override
    public void cardKeyRecieved(String key) {
        view.showWaitScreen();
        model.loadCard(key, this);
    }


    // Коллбеки
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

    @Override
    public void onDeleteSuccess(Card card) {
        view.closePage();
    }

    @Override
    public void onDeleteError(String msg) {
        view.hideProgressMessage();
        view.showErrorMsg(R.string.error_deleting_card);
    }


    // Реакция на кнопки
    @Override
    public void onEditButtonClicked() {
        view.goEditPage(currentCard);
    }

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
    public void linkView(iCardView.View view) {
        Log.d(TAG, "linkView(), view: "+view);
        this.view = view;
    }

    @Override
    public void unlinkView() {
        Log.d(TAG, "unlinkView()");
        this.view = null;
    }

    @Override
    public void linkModel(MyInterfaces.CardsService model) {
        this.model = model;
    }

    @Override
    public void unlinkModel() {
        this.model = null;
    }
}
