package ru.aakumykov.me.mvp.card_edit2;

import android.support.annotation.NonNull;

import ru.aakumykov.me.mvp.interfaces.iCardsSingleton;
import ru.aakumykov.me.mvp.models.Card;
import ru.aakumykov.me.mvp.services.CardsSingleton;


public class CardEdit2_Presenter implements iCardEdit2.Presenter {

    private iCardEdit2.View editView;

    // Обязательные методы
    @Override
    public void linkView(iCardEdit2.View view) {
        editView = view;
    }

    @Override
    public void unlinkView() {
        editView = null;
    }


    // Интерфейсные методы
    @Override
    public void saveCard() {

    }
}
