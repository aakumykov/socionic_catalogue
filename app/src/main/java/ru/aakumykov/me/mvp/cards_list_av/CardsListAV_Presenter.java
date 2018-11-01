package ru.aakumykov.me.mvp.cards_list_av;

import android.util.Log;

import java.util.List;

import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.interfaces.iAuthService;
import ru.aakumykov.me.mvp.interfaces.iCardsService;
import ru.aakumykov.me.mvp.interfaces.iDialogCallbacks;
import ru.aakumykov.me.mvp.interfaces.iTagsSingleton;
import ru.aakumykov.me.mvp.models.Card;
import ru.aakumykov.me.mvp.services.TagsSingleton;

public class CardsListAV_Presenter implements
        iCardsListAV.Presenter,
        iCardsService.ListCallbacks,
        iDialogCallbacks.Delete
{
    private final static String TAG = "CardsListAV_Presenter";
    private iCardsListAV.View view;
    private iCardsService model;
    private iAuthService authService;
    private Card currentCard;

    // Интерфейсные

    @Override
    public void loadList() {
        Log.d(TAG, "loadList()");
        model.loadList(this);
    }

    @Override
    public void deleteCard(final Card card) {
        Log.d(TAG, "deleteCard()");
        this.currentCard = card;
        view.deleteCardRequest(this);
    }


    // Коллбеки
    @Override
    public void onListLoadSuccess(List<Card> list) {
        view.displayList(list);
    }

    @Override
    public void onListLoadFail(String errorMessage) {

    }


    @Override
    public boolean deleteDialogCheck() {
        return true;
    }

    @Override
    public void deleteDialogYes() {
        Log.d(TAG, "Удаление подтверждено");
        view.showProgressBar();
//        view.showInfoMsg(R.string.deleting_card);
        model.deleteCard(currentCard, this);
    }

    @Override
    public void onDeleteDialogNo() {
        Log.d(TAG, "Удаление отклонено");
        this.currentCard = null;
    }


    @Override
    public void onDeleteSuccess(Card card) {
        Log.d(TAG, "onDeleteSuccess()");

        view.hideProgressBar();

        TagsSingleton.getInstance().updateCardTags(
                currentCard.getKey(),
                currentCard.getTags(),
                null,
                new iTagsSingleton.UpdateCallbacks() {
                    @Override
                    public void onUpdateSuccess() {
                        view.showInfoMsg(R.string.card_deleted);
                    }

                    @Override
                    public void onUpdateFail(String errorMsg) {
                        view.showErrorMsg(R.string.error_deleting_card, errorMsg);
                    }
                }
        );
    }

    @Override
    public void onDeleteError(String msg) {
        Log.d(TAG, "onDeleteError()");
        view.hideProgressBar();
        view.showErrorMsg(R.string.error_deleting_card, msg);
    }


    // Системные методы
    @Override
    public void linkView(iCardsListAV.View view) {
        this.view = view;
    }
    @Override
    public void unlinkView() {
        this.view = null;
    }

    @Override
    public void linkModel(iCardsService model) {
        this.model = model;
    }
    @Override
    public void unlinkModel() {
        this.model = null;
    }

    @Override
    public void linkAuth(iAuthService authService) {
        this.authService = authService;
    }
    @Override
    public void unlinkAuth() {
        this.authService = null;
    }


    // Внутренние методы
    private void performDeleteCard(Card card) {
        Log.d(TAG, "performDeleteCard(), "+card);

    }
}
