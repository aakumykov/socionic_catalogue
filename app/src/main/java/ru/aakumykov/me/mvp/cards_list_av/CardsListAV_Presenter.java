package ru.aakumykov.me.mvp.cards_list_av;

import android.util.Log;

import java.util.List;

import ru.aakumykov.me.mvp.card_show.iCardShow;
import ru.aakumykov.me.mvp.interfaces.iAuthService;
import ru.aakumykov.me.mvp.interfaces.iCardsService;
import ru.aakumykov.me.mvp.models.Card;

public class CardsListAV_Presenter implements
        iCardsListAV.Presenter,
        iCardsService.ListCallbacks
{
    private final static String TAG = "CardsListAV_Presenter";
    private iCardsListAV.View view;
    private iCardsService model;
    private iAuthService authService;

    // Интерфейсные

    @Override
    public void loadList() {
        Log.d(TAG, "loadList()");
        model.loadList(this);
    }

    @Override
    public void deleteCard(Card card) {

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
    public void onDeleteSuccess(Card card) {

    }

    @Override
    public void onDeleteError(String msg) {

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

}
