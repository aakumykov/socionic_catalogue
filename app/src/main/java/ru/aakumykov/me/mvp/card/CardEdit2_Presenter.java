package ru.aakumykov.me.mvp.card;

import android.content.Intent;

import ru.aakumykov.me.mvp.interfaces.iAuthSingleton;
import ru.aakumykov.me.mvp.interfaces.iCardsSingleton;
import ru.aakumykov.me.mvp.services.AuthSingleton;
import ru.aakumykov.me.mvp.services.CardsSingleton;

public class CardEdit2_Presenter implements iCardEdit2.Presenter {

    private final static String TAG = "CardEdit2_View";
    private iCardEdit2.View view;
    private iCardsSingleton cardsService = CardsSingleton.getInstance();
    private iAuthSingleton authService = AuthSingleton.getInstance();

    @Override
    public void linkView(iCardEdit2.View view) {
        this.view = view;
    }

    @Override
    public void unlinkView() {
        this.view = null;
    }

    @Override
    public void processInputIntent(Intent intent) throws Exception {

    }

    @Override
    public void saveCard() throws Exception {

    }
}
