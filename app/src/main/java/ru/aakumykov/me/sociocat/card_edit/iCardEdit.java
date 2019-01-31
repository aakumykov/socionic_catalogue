package ru.aakumykov.me.sociocat.card_edit;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.List;

import ru.aakumykov.me.sociocat.iBaseView;
import ru.aakumykov.me.sociocat.models.Card;

public interface iCardEdit {

    interface TagProcessCallbacks {
        void onTagProcessed();
    }

    interface TagsListCallbacks {
        void onTagsListSuccess(List<String> list);
        void onTagsListFail(String errorMsg);
    }


    interface View extends iBaseView {
        void displayCard(Card card);

        void showModeSwitcher();
        void hideModeSwitcher();

        void displayTitle(String text);
        void displayQuote(String text);
        void displayImage(String imageURI, boolean unprocessedYet);
        void displayImageBitmap(Bitmap bitmap);
        void displayVideo(String videoCode);
//        void displayAudio(Uri dataURI);

        String getCardTitle();
        String getCardQuote();
        String getCardDescription();

        HashMap<String,Boolean> getCardTags();

        Bitmap getImageBitmap();

        void addTag(String tag);

        void storeCardVideoCode(String videoCode);
        String getCardVideoCode();

        void showImageProgressBar();
        void hideImageProgressBar();

        void setImageUploadProgress(int progress);
        void showBrokenImage();

        void disableForm();
        void enableForm();

        void finishEdit(Card card, boolean showAfterFinish);

        void goCardShow(Card card);

        String detectMimeType(Uri dataURI);

        Context getApplicationContext();
    }

    interface Presenter {
        void linkView(View view);
        void unlinkView();

        void beginWork(@Nullable Intent intent);

        void processRecievedData(String mode, Intent intent) throws Exception;
        void processLinkToImage(@Nullable Intent intent) throws Exception;
        void processIncomingImage(@Nullable Intent intent) throws Exception;

        void loadTagsList(TagsListCallbacks callbacks);

        void processTagInput(String tag);
        void processTagInput(String tag, TagProcessCallbacks callbacks);

        void setCardType(String cardType);
        void saveCard() throws Exception;
    }
}
