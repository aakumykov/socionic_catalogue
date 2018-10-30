package ru.aakumykov.me.mvp.cards_list_av;

import android.util.Log;

import ru.aakumykov.me.mvp.card_show.iCardShow;
import ru.aakumykov.me.mvp.interfaces.iAuthService;
import ru.aakumykov.me.mvp.interfaces.iCardsService;
import ru.aakumykov.me.mvp.models.Card;

public class CardsListAV_Presenter implements
        iCardsListAV.Presenter
{
    private final static String TAG = "CardsListAV_Presenter";
    private iCardsListAV.View view;
    private iCardsService model;
    private iAuthService authService;

    // Основные методы
    @Override
    public void deleteCard(Card card) {

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
