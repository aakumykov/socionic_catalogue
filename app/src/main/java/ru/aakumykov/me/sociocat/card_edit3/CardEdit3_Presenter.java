package ru.aakumykov.me.sociocat.card_edit3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import com.google.gson.Gson;

import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.interfaces.iCardsSingleton;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.services.CardsSingleton;

public class CardEdit3_Presenter implements iCardEdit3.Presenter {

    private iCardEdit3.View view;
    private iCardsSingleton cardsService = CardsSingleton.getInstance();


    // Системные методы (условно)
    @Override
    public void linkView(iCardEdit3.View view) {
        this.view = view;
    }

    @Override
    public void unlinkView() {
        this.view = null;

//        SharedPreferences sharedPreferences = view.getSharedPrefs(Constants.SHARED_PREFERENCES_CARD_EDIT);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//
//        Gson gson = new Gson();
//        String json = gson.toJson(currentCard);
//        editor.putString(Constants.CARD, json);
//        editor.apply();
    }


    // Интерфейсные методы
    @Override
    public void processInputIntent(@Nullable Intent intent) throws Exception {
        if (null == intent)
            throw new IllegalArgumentException("Intent is NULL");

        String cardKey = intent.getStringExtra(Constants.CARD_KEY);
        if (null == cardKey)
            throw new IllegalArgumentException("There is no cardKey in Intent");

        cardsService.loadCard(cardKey, new iCardsSingleton.LoadCallbacks() {

            @Override
            public void onCardLoadSuccess(Card card) {
                if (null != view)
                    view.displayCard(card);
            }

            @Override
            public void onCardLoadFailed(String msg) {
                if (null != view)
                    view.showErrorMsg(R.string.CARD_EDIT_error_loading_card, msg);
            }
        });
    }

    @Override
    public void saveCard() {

    }
}
