package ru.aakumykov.me.sociocat.card_edit3;

import android.content.Intent;
import android.support.annotation.Nullable;

import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.models.Card;

public class CardEdit3_Presenter implements iCardEdit3.Presenter {

    private iCardEdit3.View view;


    // Системные методы (условно)
    @Override
    public void linkView(iCardEdit3.View view) {
        this.view = view;
    }

    @Override
    public void unlinkView() {
        this.view = null;
    }


    // Интерфейсные методы
    @Override
    public void processInputIntent(@Nullable Intent intent) throws Exception {
        if (null == intent)
            throw new IllegalArgumentException("Intent is NULL");

        Card card = intent.getParcelableExtra(Constants.CARD);
        if (null == card)
            throw new IllegalArgumentException("There is no Card in Intent");

        if (null != view)
            view.displayCard(card);
    }

    @Override
    public void saveCard() {

    }
}
