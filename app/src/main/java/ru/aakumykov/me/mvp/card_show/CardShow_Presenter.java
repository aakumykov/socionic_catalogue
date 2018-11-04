package ru.aakumykov.me.mvp.card_show;

import android.util.Log;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.interfaces.iAuthSingleton;
import ru.aakumykov.me.mvp.interfaces.iCardsSingleton;
import ru.aakumykov.me.mvp.models.Card;
import ru.aakumykov.me.mvp.services.TagsSingleton;

public class CardShow_Presenter implements
        iCardShow.Presenter,
        iCardsSingleton.CardCallbacks
{

    private final static String TAG = "CardShow_Presenter";
    private iCardShow.View view;
    private iCardsSingleton model;
    private iAuthSingleton authService;

    private Card currentCard;


    CardShow_Presenter() {}


    // Получение карточки
    @Override
    public void cardKeyRecieved(String key) {
        view.showWaitScreen();
        model.loadCard(key, this);
    }


    // Реакция на кнопки
    @Override
    public void onTagClicked(String tagName) {
        view.goList(tagName);
    }

    @Override
    public void onEditButtonClicked() {
        view.goEditPage(currentCard);
    }

    @Override
    public void onDeleteButtonClicked() {
        Log.d(TAG, "onDeleteButtonClicked()");
//        if (authService.isAuthorized()) view.showDeleteDialog();
//        else view.showErrorMsg(R.string.not_authorized);
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


    // Link / Unlink
    @Override
    public void linkView(iCardShow.View view) {
        Log.d(TAG, "linkView(), view: "+view);
        this.view = view;
    }
    @Override
    public void unlinkView() {
        Log.d(TAG, "unlinkView()");
        this.view = null;
    }

    @Override
    public void linkCardsService(iCardsSingleton model) {
        this.model = model;
    }
    @Override
    public void unlinkCardsService() {
        this.model = null;
    }

    @Override
    public void linkAuth(iAuthSingleton authService) {
        this.authService = authService;
    }
    @Override
    public void unlinkAuthService() {
        this.authService = null;
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
        TagsSingleton.getInstance().updateCardTags(card.getKey(), card.getTags(), null, null);
        view.closePage();
    }

    @Override
    public void onDeleteError(String msg) {
        view.hideProgressMessage();
        view.showErrorMsg(R.string.error_deleting_card);
    }

}
