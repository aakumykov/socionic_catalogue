package ru.aakumykov.me.sociocat.card_edit;

import android.content.Intent;
import android.graphics.Bitmap;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.List;

import ru.aakumykov.me.sociocat.interfaces.iBaseView;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.utils.ImageType;

public interface iCardEdit {

    interface TagsListLoadCallbacks {
        void onTagsListLoadSuccess(List<String> list);
        void onTagsListLoadFail(String errorMsg);
    }

    interface View extends iBaseView {

        // "Большие" методы
        void displayCard(Card card);

        void disableForm();
        void enableForm();

        // Картинка
        void pickImage();

        <T> void displayImage(T imageURI);
        void removeImage();

        boolean hasImage();

        void showImageError(int msgId);
        void hideImageError();

        void showImageThrobber();
        void hideImageThrobber();

        // Мультимедиа
        void displayVideo(String videoCode, @Nullable Float timecode);
        void displayAudio(String audioCode, @Nullable Float timecode);

        float pauseMedia();
        void resumeMedia(float position);
        void removeMedia();

        void convert2audio();
        void convert2video();

        // Получение денных формы
        String getCardTitle();
        String getQuote();
        String getQuoteSource();
        String getDescription();
        Float getTimecode();
        HashMap<String,Boolean> getTags();

        // Проверка формы
        boolean isFormFilled();

        void showTitleError(int msgId);
        void showQuoteError(int msgId);
        void showVideoError(int msgId);
        void showAudioError(int msgId);
        void showMediaError();
        void hideMediaError();
        void showDescriptionError(int msgId);

        // Разное
        void finishEdit(Card card);
        void addTag(String tag);
    }

    interface Presenter {
        void linkView(View view);
        void unlinkView();

        void onViewPaused();
        void onViewResumed();

        void onIntentReceived(@Nullable Intent intent);
        void onConfigurationChanged();

        void loadTagsList(TagsListLoadCallbacks callbacks);
        void processTag(String tag);

        void removeImageClicked();
        void restoreImageClicked();

        void removeMediaClicked();

        void convert2audio();
        void convert2video();

        void processYoutubeLink(String youtubeLink) throws Exception;
        void saveCard(boolean alreadyValidated) throws Exception;

        void clearEditState();

        boolean hasCard();

        void onImageSelectionSuccess(Bitmap bitmap, ImageType imageType);
        void onImageSelectionError(String errorMsg);

        void onImageViewClicked();
    }
}
