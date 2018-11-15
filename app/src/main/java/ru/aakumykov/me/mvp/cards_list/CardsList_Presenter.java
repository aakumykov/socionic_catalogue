package ru.aakumykov.me.mvp.cards_list;

import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;

import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.interfaces.iAuthSingleton;
import ru.aakumykov.me.mvp.interfaces.iCardsSingleton;
import ru.aakumykov.me.mvp.interfaces.iDialogCallbacks;
import ru.aakumykov.me.mvp.interfaces.iTagsSingleton;
import ru.aakumykov.me.mvp.models.Card;
import ru.aakumykov.me.mvp.services.AuthSingleton;
import ru.aakumykov.me.mvp.services.CardsSingleton;
import ru.aakumykov.me.mvp.services.TagsSingleton;

public class CardsList_Presenter implements
        iCardsList.Presenter,
        iCardsSingleton.ListCallbacks,
        iCardsSingleton.DeleteCallbacks,
        iDialogCallbacks.Delete
{
    private final static String TAG = "CardsList_Presenter";
    private iCardsList.View view;
    private iCardsSingleton cardsService = CardsSingleton.getInstance();
    private iAuthSingleton authService = AuthSingleton.getInstance();

    private Card currentCard = null;
    private String tagFilter = null;

    // Системные методы
    @Override
    public void linkView(iCardsList.View view) {
        this.view = view;
    }
    @Override
    public void unlinkView() {
        this.view = null;
    }


    // Интерфейсные
    @Override
    public void loadList(@Nullable String tagFilter) {
        Log.d(TAG, "loadList()");

        if (null != tagFilter) {
            this.tagFilter = tagFilter;
            cardsService.loadList(tagFilter,this);
        }
        else {
            this.tagFilter = null;
            cardsService.loadList(this);
        }
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
        if (null != tagFilter)
            view.displayTagFilter(tagFilter);
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
        cardsService.deleteCard(currentCard, this);
    }

    @Override
    public void onDeleteDialogNo() {
        Log.d(TAG, "Удаление отклонено");
        this.currentCard = null;
    }


    @Override
    public void onCardDeleteSuccess(Card card) {
        Log.d(TAG, "onCardDeleteSuccess()");

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
                        view.showErrorMsg(R.string.CARD_SHOW_error_deleting_card, errorMsg);
                    }
                }
        );
    }

    @Override
    public void onCardDeleteError(String msg) {
        Log.d(TAG, "onCardDeleteError()");
        view.hideProgressBar();
        view.showErrorMsg(R.string.CARD_SHOW_error_deleting_card, msg);
    }


    // Внутренние методы
    private void performDeleteCard(Card card) {
        Log.d(TAG, "performDeleteCard(), "+card);

    }
}
