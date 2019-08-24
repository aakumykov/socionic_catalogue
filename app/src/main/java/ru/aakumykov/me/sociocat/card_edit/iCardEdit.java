package ru.aakumykov.me.sociocat.card_edit;

import android.content.Intent;
import android.graphics.Bitmap;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.List;

import ru.aakumykov.me.sociocat.interfaces.iBaseView;
import ru.aakumykov.me.sociocat.models.Card;

public interface iCardEdit {

    interface TagsListLoadCallbacks {
        void onTagsListLoadSuccess(List<String> list);
        void onTagsListLoadFail(String errorMsg);
    }

    interface View extends iBaseView {
        void displayCard(Card card);
        void displayImage(String imageURI);
        void displayVideo(String videoCode, @Nullable Float timecode);
        void displayAudio(String audioCode, @Nullable Float timecode);

        void removeImage();
        void removeMedia();

        String getCardTitle();
        String getQuote();
        String getQuoteSource();
        Bitmap getImageBitmap();
        String getDescription();
        Float getTimecode();
        HashMap<String,Boolean> getTags();

        void convert2audio();
        void convert2video();

        void showTitleError(int msgId);
        void showQuoteError(int msgId);
        void showVideoError(int msgId);
        void showAudioError(int msgId);
        void showMediaError();
        void hideMediaError();
        void showDescriptionError(int msgId);

        void showImageError(int msgId);
        void hideImageError();

        void disableForm();
        void enableForm();

        void showImageProgressBar();
        void hideImageProgressBar();

        boolean isFormFilled();

        void finishEdit(Card card);
        void showCard(Card card);

        void addTag(String tag);

        void showDraftRestoreDialog(Card cardDraft);
    }

    interface Presenter {
        void linkView(View view);
        void unlinkView();

        void processInputIntent(@Nullable Intent intent) throws Exception;
        void loadTagsList(TagsListLoadCallbacks callbacks);
        void processTag(String tag);

        void removeImageClicked();
        void removeMedia();

        void convert2audio();
        void convert2video();

        void processSelectedImage(@Nullable Intent data) throws Exception;
        void processYoutubeLink(String youtubeLink) throws Exception;
        void saveCard(boolean alreadyValidated) throws Exception;

        void saveEditState();
        void restoreEditState() throws Exception;
        void clearEditState();
    }
}
