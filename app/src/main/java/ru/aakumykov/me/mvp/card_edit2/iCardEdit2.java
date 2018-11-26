package ru.aakumykov.me.mvp.card_edit2;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.HashMap;

import ru.aakumykov.me.mvp.iBaseView;
import ru.aakumykov.me.mvp.interfaces.iCardsSingleton;
import ru.aakumykov.me.mvp.models.Card;

public interface iCardEdit2 {

    interface View extends iBaseView {
        void showModeSwitcher();
        void hideModeSwitcher();

        void switchTextMode(@Nullable Card card);
        void switchImageMode(@Nullable Card card);
        void switchVideoMode(@Nullable Card card);
        void switchAudioMode(@Nullable Card card);

        String getCardTitle();
        String getCardQuote();
        String getVideoCode();
        String getCardDescription();
//        HashMap<String,Boolean> getCardTags();

        void disableForm();
        void enableForm();

        void addTag(String tagName);

        void finishEdit(Card updatedCard);
    }

    interface Presenter {
        void linkView(iCardEdit2.View view);
        void unlinkView();

        void setCardType(String cardType);

        void processTag(String tagName);

        void processInputIntent(@Nullable Intent intent);
        void saveCard();
    }
}
