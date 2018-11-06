package ru.aakumykov.me.mvp.card;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;

import java.net.URI;

import ru.aakumykov.me.mvp.iBaseView;
import ru.aakumykov.me.mvp.models.Card;

public interface iCardEdit2 {

    interface View extends iBaseView {
        void displayCard(Card card);

        void showImageProgressBar();
        void hideImageProgressBar();
        void setImageUploadProgress(int progress);
        void disableForm();
        void enableForm();


        void displayQuote(String text);

        void displayImage(Uri imageURI);
        void showBrokenImage();

        void finishEdit(Card card); // нужен?

        String getCardTitle();
        String getCardQuote();
        String getCardDescription();
    }

    interface Presenter {
        void linkView(View view);
        void unlinkView();

        void processInputIntent(Intent intent) throws Exception;
        void processIncomingImage(Intent data);
        void saveCard() throws Exception;

        void forgetSelectedFile();
    }
}
