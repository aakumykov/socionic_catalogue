package ru.aakumykov.me.sociocat.card_edit3;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.List;

import ru.aakumykov.me.sociocat.interfaces.iBaseView;
import ru.aakumykov.me.sociocat.models.Card;

public interface iCardEdit3 {

    interface TagsListLoadCallbacks {
        void onTagsListLoadSuccess(List<String> list);
        void onTagsListLoadFail(String errorMsg);
    }

    interface View extends iBaseView {
        void displayCard(Card card);
        void displayImage(String imageURI);
        void displayVideo(String videoCode);
        void displayAudio(String audioCode);

        void removeImage();

        String getCardTitle();
        String getQuote();
        String getQuoteSource();
        Bitmap getImageBitmap();
        String getDescription();
        HashMap<String,Boolean> getTags();

        void convert2audio();
        void convert2video();

        void showTitleError(int msgId);
        void showQuoteError(int msgId);
        void showImageError(int msgId);
        void showVideoError(int msgId);
        void showAudioError(int msgId);
        void showDescriptionError(int msgId);

        void disableForm();
        void enableForm();

        void showImageProgressBar();
        void hideImageProgressBar();

        boolean formIsFilled();

        void finishEdit(Card card);
        void showCard(Card card);

        void addTag(String tag);
    }

    interface Presenter {
        void linkView(View view);
        void unlinkView();

        void processInputIntent(@Nullable Intent intent) throws Exception;
        void loadTagsList(TagsListLoadCallbacks callbacks);
        void processTag(String tag);

        void convertToAudio();
        void convertToVideo();

        void processIncomingImage(@Nullable Intent intent) throws Exception;
        void processVideoLink(String videoString);
        void processAudioLink(String audioString);
        void saveCard(boolean alreadyValidated) throws Exception;

        void saveEditState();
        void restoreEditState() throws Exception;
        void clearEditState();
    }
}
