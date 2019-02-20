package ru.aakumykov.me.sociocat.card_edit3;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;

import ru.aakumykov.me.sociocat.interfaces.iBaseView;
import ru.aakumykov.me.sociocat.models.Card;

public interface iCardEdit3 {

    interface View extends iBaseView {
        void displayCard(Card card);
        void displayImage(String imageURI);

        String getCardTitle();
        String getQuote();
        String getQuoteSource();
        Bitmap getImageBitmap();
        String getDescription();

        void showImageProgressBar();
        void hideImageProgressBar();

        void finishEdit(Card card);
    }

    interface Presenter {
        void linkView(View view);
        void unlinkView();

        void processInputIntent(@Nullable Intent intent) throws Exception;
        void processSelectedImage(int resultCode, @Nullable Intent intent) throws Exception;
        void saveCard() throws Exception;

        void saveEditState();
        void restoreEditState();
        void clearEditState();
    }
}
