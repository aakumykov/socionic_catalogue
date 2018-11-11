package ru.aakumykov.me.mvp.card;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;

import java.net.URI;
import java.util.HashMap;

import ru.aakumykov.me.mvp.iBaseView;
import ru.aakumykov.me.mvp.models.Card;

public interface iCardEdit2 {

    interface View extends iBaseView {
        void displayCard(Card card);

        void showModeSwitcher();
        void hideModeSwitcher();

        void displayQuote(String text);
        void displayImage(Uri imageURI);
//        void displayAudio(Uri dataURI);
//        void displayVideo(Uri dataURI);

        String getCardTitle();
        String getCardQuote();
//        Uri getCardImageURI();
        String getCardDescription();
        String getNewTag();
        HashMap<String,Boolean> getCardTags();

        void showImageProgressBar();
        void hideImageProgressBar();

        void setImageUploadProgress(int progress);
        void showBrokenImage();

        void disableForm();
        void enableForm();

        void finishEdit(Card card); // нужен?

        void goCardShow(Card card);

        String detectMimeType(Uri dataURI);
    }

    interface Presenter {
        void linkView(View view);
        void unlinkView();

        void makeStartDecision(@Nullable Intent intent);

        void processInputData(String mode, Intent intent) throws Exception;

        void onAddTagButtonClicked();

        void setCardType(String cardType);
        void saveCard() throws Exception;

        void forgetSelectedFile();
    }
}
