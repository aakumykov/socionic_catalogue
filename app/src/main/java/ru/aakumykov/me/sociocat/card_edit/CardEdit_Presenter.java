package ru.aakumykov.me.sociocat.card_edit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.aakumykov.me.sociocat.AppConfig;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.constants.Constants;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.models.Tag;
import ru.aakumykov.me.sociocat.singletons.AuthSingleton;
import ru.aakumykov.me.sociocat.singletons.CardsSingleton;
import ru.aakumykov.me.sociocat.singletons.StorageSingleton;
import ru.aakumykov.me.sociocat.singletons.TagsSingleton;
import ru.aakumykov.me.sociocat.singletons.UsersSingleton;
import ru.aakumykov.me.sociocat.singletons.iCardsSingleton;
import ru.aakumykov.me.sociocat.singletons.iStorageSingleton;
import ru.aakumykov.me.sociocat.singletons.iTagsSingleton;
import ru.aakumykov.me.sociocat.singletons.iUsersSingleton;
import ru.aakumykov.me.sociocat.utils.ImageType;
import ru.aakumykov.me.sociocat.utils.ImageUtils;
import ru.aakumykov.me.sociocat.utils.IntentUtils;
import ru.aakumykov.me.sociocat.utils.MVPUtils.MVPUtils;
import ru.aakumykov.me.sociocat.utils.MyUtils;
import ru.aakumykov.me.sociocat.utils.NetworkUtils;

public class CardEdit_Presenter implements
        iCardEdit.Presenter,
        iCardsSingleton.SaveCardCallbacks
{
    private float mediaPosition = 0.0f;

    private static final String TAG = "CardEdit_Presenter";
    private iCardEdit.View view;

    private final iUsersSingleton usersSingleton = UsersSingleton.getInstance();
    private final iCardsSingleton cardsSingleton = CardsSingleton.getInstance();
    private final iTagsSingleton tagsSingleton = TagsSingleton.getInstance();
    private final iStorageSingleton storageSingleton = StorageSingleton.getInstance();

    private Card currentCard;
    private Card oldCard;

    private boolean isNewCard = false;

    private Bitmap mImageBitmap;
    private ImageType mImageType;


    // Системные методы (условно)
    @Override
    public void linkView(iCardEdit.View view) {
        this.view = view;
//        sharedPreferences = view.getSharedPrefs(Constants.SHARED_PREFERENCES_CARD_EDIT);
    }

    @Override
    public void unlinkView() {
        this.view = new CardEdit_ViewStub();
    }

    @Override
    public void onViewPaused() {
        updateCurrentCardFromView();
        mediaPosition = view.pauseMedia();
    }

    @Override
    public void onViewResumed() {
        view.resumeMedia(mediaPosition);
    }


    // Интерфейсные методы
    @Override
    public void onFirstOpen(@Nullable Intent intent) {
        if (NetworkUtils.isOffline(view.getAppContext())) {
            view.showToast(R.string.CARD_EDIT_impossible_without_network_connection);
            view.closePage();
            return;
        }

        if (null == intent) {
            view.showErrorMsg(R.string.data_error, "Intent is null");
            return;
        }

        if (!AuthSingleton.isLoggedIn()) {
            view.requestLogin(intent);
            return;
        }

        String action = String.valueOf(intent.getAction());

        try {
            switch (action) {
                case Constants.ACTION_CREATE:
                case Intent.ACTION_CREATE_DOCUMENT:
                    proceed2createCard(intent);
                    break;

                case Constants.ACTION_EDIT:
                case Intent.ACTION_EDIT:
                    proceed2editCard(intent);
                    break;

                default:
                    throw new IllegalArgumentException("Unknown action: "+action);
            }
        }
        catch (Exception e) {
            view.showErrorMsg(R.string.CARD_EDIT_error_processing_data, e.getMessage());
        }
    }

    @Override
    public void onConfigurationChanged() {

        boolean hasLocalImage = (null != mImageBitmap);

        view.displayCard(currentCard, hasLocalImage);

        displayLocalImage();
    }

    @Override
    public void loadTagsList(final iCardEdit.TagsListLoadCallbacks callbacks) {
        tagsSingleton.listTags(new iTagsSingleton.ListCallbacks() {

            @Override
            public void onTagsListSuccess(List<Tag> tagsList) {
                List<String> list = new ArrayList<>();
                for (int i=0; i<tagsList.size(); i++) {
                    String tag = tagsList.get(i).getName();
                    if (!list.contains(tag)) list.add(tag);
                }
                callbacks.onTagsListLoadSuccess(list);
            }

            @Override
            public void onTagsListFail(String errorMsg) {
                callbacks.onTagsListLoadFail(errorMsg);
            }
        });
    }

    @Override
    public void processTag(String tag) {
        tag = MVPUtils.normalizeTag(tag);
        if (!TextUtils.isEmpty(tag)) {
            view.addTag(tag);
        }
    }

    @Override
    public void removeImageClicked() {
        //view.displayImage(currentCard.getImageURL());
        view.removeImage();
        mImageBitmap = null;
        mImageType = null;
    }

    @Override
    public void restoreImageClicked() {
        view.displayImage(currentCard.getImageURL());
    }

    @Override
    public void removeMediaClicked() {
        if (currentCard.isAudioCard())
            currentCard.removeAudioCode();

        if (currentCard.isVideoCard())
            currentCard.removeVideoCode();

        view.removeMedia();
    }

    @Override
    public void convert2audio() {
        currentCard.setType(Constants.AUDIO_CARD);
        currentCard.setAudioCode(currentCard.getVideoCode());
        currentCard.removeVideoCode();
        view.convert2audio();
    }

    @Override
    public void convert2video() {
        currentCard.setType(Constants.VIDEO_CARD);
        currentCard.setVideoCode(currentCard.getAudioCode());
        currentCard.removeAudioCode();
        view.convert2video();
    }

    @Override
    public void processYoutubeLink(String youtubeLink) throws Exception {
        String youtubeCode = MVPUtils.extractYoutubeVideoCode(youtubeLink);

        if (null != youtubeCode) {
            if (currentCard.isVideoCard()) {
                currentCard.setVideoCode(youtubeCode);
                view.displayVideo(youtubeCode, 0.0f);
            }
            else {
                currentCard.setAudioCode(youtubeCode);
                view.displayAudio(youtubeCode, 0.0f);
            }
        }
        else {
            view.showErrorMsg(R.string.CARD_EDIT_error_adding_video, "Wrong video code: "+ youtubeLink);
        }
    }

    @Override
    public void clearEditState() {
        //view.clearSharedPrefs(sharedPreferences, Constants.CARD);
        MVPUtils.clearCardDraft(view.getAppContext());
    }

    @Override
    public boolean hasCard() {
        return null != currentCard;
    }

    @Override
    public void onImageSelectionSuccess(Bitmap bitmap, ImageType imageType) {
        this.mImageType = imageType;
        this.mImageBitmap = bitmap.copy(bitmap.getConfig(), true);
        view.displayImage(bitmap);
    }

    @Override
    public void onImageSelectionError(String errorMsg) {
        view.showErrorMsg(R.string.error_selecting_image, errorMsg);
    }

    @Override
    public void onImageViewClicked() {
        view.pickImage();
    }

    @Override
    public void saveCard(boolean alreadyValidated) throws Exception {

        if (!alreadyValidated) {
            updateCurrentCardFromView();
            processBeforeSave();
            if (!formIsValid())
                return;
        }

        view.disableForm();

        // Сохраняю картинку, если этого ещё не сделано
        if (null != mImageBitmap && null != mImageType) {
            uploadImage(new iStorageSingleton.ImageUploadCallbacks() {
                @Override
                public void onImageUploaded() {
                    try {
                        saveCard(true);
                    }
                    catch (Exception e) {
                        view.showErrorMsg(R.string.CARD_EDIT_error_saving_card, e.getMessage());
                        MyUtils.printError(TAG, e);
                    }
                }
            });
        }
        else {
            Long currentTime = new Date().getTime();
            if (isNewCard) {
                setAuthorInfo();
                currentCard.setCTime(currentTime);
            }

            currentCard.setMTime(currentTime);

            view.showProgressMessage(R.string.CARD_EDIT_saving_card);

            cardsSingleton.saveCard(currentCard, oldCard, this);
        }
    }

    // Коллбеки
    @Override public void onCardSaveSuccess(Card card) {

        finishWork(card);

        //updateCardTags(card);

        /*if (editMode.equals(CardEditMode.CREATE)) {

            MVPUtils.subscribeToTopicNotifications(
                    view.getGlobalContext(),
                    card.getKey(),
                    new MVPUtils.TopicNotificationsCallbacks.SubscribeCallbacks() {
                        @Override
                        public void onSubscribeSuccess() {

                        }

                        @Override
                        public void onSubscribeFail(String errorMsg) {
                            view.showToast(R.string.CARD_EDIT_error_subscribing_to_comments);
                            Log.e(TAG, errorMsg);
                            //view.showErrorMsg(R.string.CARD_EDIT_error_subscribing_to_comments, errorMsg);
                        }
                    }
            );
        }*/
    }

    @Override public void onCardSaveError(String message) {
         {
            view.showErrorMsg(R.string.CARD_EDIT_error_saving_card, message);
            view.enableForm();
        }
    }


    // Внутренние методы
    private void proceed2createCard(Intent intent) {

        isNewCard = true;
        currentCard = new Card(cardsSingleton.createKey());

        String cardType = String.valueOf(intent.getStringExtra(Constants.CARD_TYPE));
        Intent externalDataIntent = intent.getParcelableExtra(Intent.EXTRA_INTENT);

        view.focusFirstField(true);

        switch (cardType) {
            case Constants.TEXT_CARD:
                prepareForTextCard(externalDataIntent);
                break;
            case Constants.IMAGE_CARD:
                prepareForImageCard(externalDataIntent);
                break;
            case Constants.AUDIO_CARD:
                prepareForAudioCard(externalDataIntent);
                break;
            case Constants.VIDEO_CARD:
                prepareForVideoCard(externalDataIntent);
                break;
            default:
                throw new IllegalArgumentException("Unknown cardType: "+cardType);
        }
    }

    private void prepareForTextCard(@Nullable Intent dataIntent) {
        currentCard.setType(Constants.TEXT_CARD);

        String quote = IntentUtils.extractText(dataIntent);
        String title = MyUtils.cutToLength(quote, AppConfig.TITLE_MAX_LENGTH);

        view.prepareForTextCard(title, quote);
    }

    private void prepareForImageCard(@Nullable Intent dataIntent) {
        currentCard.setType(Constants.IMAGE_CARD);

        IntentUtils.extractImage(view.getAppContext(), dataIntent, new IntentUtils.ImageExtractionCallbacks() {
            @Override
            public void onImageExtractionSuccess(Bitmap bitmap, ImageType imageType, Uri imageURI) {
                // TODO: вынести в отдельный метод
                mImageBitmap = bitmap.copy(bitmap.getConfig(), true);
                mImageType = imageType;

                view.prepareForImageCard(mImageBitmap);
            }

            @Override
            public void onImageExtractionError(String errorMsg) {
                if (!isNewCard) {
                    view.showLongToast(R.string.CARD_EDIT_error_processing_image);
                    Log.e(TAG, errorMsg);
                }
                view.prepareForImageCard(null);
            }
        });
    }

    private void prepareForVideoCard(@Nullable Intent dataIntent) {
        currentCard.setType(Constants.VIDEO_CARD);

        String videoCode = IntentUtils.extractYoutubeVideoCode(dataIntent);
        currentCard.setVideoCode(videoCode);
        view.prepareForVideoCard(videoCode, null);
    }

    private void prepareForAudioCard(@Nullable Intent dataIntent) {
        currentCard.setType(Constants.AUDIO_CARD);

        String audioCode = IntentUtils.extractYoutubeVideoCode(dataIntent);
        currentCard.setVideoCode(audioCode);
        view.prepareForAudioCard(audioCode, null);
    }

    private void proceed2editCard(Intent intent) throws Exception {

        isNewCard = false;
        currentCard = intent.getParcelableExtra(Constants.CARD);

        view.focusFirstField(false);

        if (null == currentCard) {
            view.showErrorMsg(R.string.data_error, "Failed to get Card from Intent");
            Log.e(TAG, "Intent: "+intent);
            Bundle extras = intent.getExtras();
            if (null != extras) {
                Log.e(TAG, "Intent extras:");
                for (String key : extras.keySet())
                    Log.e(TAG, key + ": " + extras.get(key));
            }
            return;
        }

        String cardType = String.valueOf(currentCard.getType());

        switch (cardType) {
            case Constants.TEXT_CARD:
            case Constants.IMAGE_CARD:
            case Constants.VIDEO_CARD:
            case Constants.AUDIO_CARD:
                view.displayCard(currentCard, false);
                break;
            default:
                throw new Exception("Unknown card type: "+cardType);
        }
    }

    private void displayLocalImage() {
        if (null != mImageBitmap)
            view.displayImage(mImageBitmap);
    }

    private void updateCurrentCardFromView() {
        if (null != currentCard) {
            currentCard.setTitle(view.getCardTitle());
            currentCard.setQuote(view.getQuote());
            currentCard.setQuoteSource(view.getQuoteSource());
            currentCard.setDescription(view.getDescription());
            currentCard.setTags(new ArrayList<>(view.getTags().keySet()));
            currentCard.setTimecode(view.getTimecode());
        }
    }

    private void processBeforeSave() {
        String title = currentCard.getTitle().trim();
        currentCard.setTitle(title);

        String quote = currentCard.getQuote().trim();
        currentCard.setQuote(quote);

        String quoteSource = currentCard.getQuoteSource().trim();
        currentCard.setQuoteSource(quoteSource);

        String description = currentCard.getDescription().trim();
        currentCard.setDescription(description);

        if (quote.length() > AppConfig.LONG_TAG_THRESHOLD) {
            String longTag = view.getString(R.string.TAG_long_text);
            currentCard.addTag(longTag);
        }
    }

    private void setAuthorInfo() {
        currentCard.setUserId(AuthSingleton.currentUserId());
        currentCard.setUserName(usersSingleton.currentUserName());
        currentCard.setUserAvatarURL(usersSingleton.getCurrentUser().getAvatarURL());
    }

    private boolean formIsValid() {
        boolean valid = true;

        // Название
        String title = view.getCardTitle().trim();
        if (TextUtils.isEmpty(title)) {
            view.showTitleError(R.string.cannot_be_empty);
            return false;
        } else {
            if (title.length() < AppConfig.TITLE_MIN_LENGTH) {
                view.showTitleError(R.string.CARD_EDIT_title_too_short);
                valid = false;
            }
            if (title.length() > AppConfig.TITLE_MAX_LENGTH) {
                //int lengthOvershoot = title.length() - Constants.TITLE_MAX_LENGTH;
                view.showTitleError(R.string.CARD_EDIT_title_too_long);
                valid = false;
            }
        }

        // Цитата
        if (currentCard.isTextCard()) {
            String quote = view.getQuote().trim();
            if (TextUtils.isEmpty(quote)) {
                view.showQuoteError(R.string.cannot_be_empty);
                return false;
            } else {
                if (quote.length() < AppConfig.QUOTE_MIN_LENGTH) {
                    view.showQuoteError(R.string.CARD_EDIT_quote_too_short);
                    valid = false;
                }
                if (quote.length() > AppConfig.QUOTE_MAX_LENGTH) {
                    //int lengthOvershoot = title.length() - Constants.TITLE_MAX_LENGTH;
                    view.showQuoteError(R.string.CARD_EDIT_quote_too_long);
                    valid = false;
                }
            }
        }

        // Описание
        String description = view.getDescription().trim();
        if (TextUtils.isEmpty(description)) {
            view.showDescriptionError(R.string.cannot_be_empty);
            return false;
        } else {
            if (description.length() < AppConfig.DESCRIPTION_MIN_LENGTH) {
                view.showDescriptionError(R.string.CARD_EDIT_description_too_short);
                valid = false;
            }
            if (description.length() > AppConfig.DESCRIPTION_MAX_LENGTH) {
                //int lengthOvershoot = title.length() - Constants.TITLE_MAX_LENGTH;
                view.showDescriptionError(R.string.CARD_EDIT_description_too_long);
                valid = false;
            }
        }

        // Картинка
        if (currentCard.isImageCard()) {
            if (!view.hasImage()) {
                valid = false;
                view.showImageError(R.string.CARD_EDIT_you_must_select_image);
            }
        }

        // Видео
        if (currentCard.isVideoCard()) {
            if (null == currentCard.getVideoCode()) {
                valid = false;
                view.showVideoError(R.string.CARD_EDIT_there_is_no_video);
                view.showMediaError();
            } else if (!MVPUtils.isYoutubeLink(currentCard.getVideoCode())) {
                valid = false;
                view.showVideoError(R.string.CARD_EDIT_incorrect_video_code);
                view.showMediaError();
            }
        }

        // Аудио
        if (currentCard.isAudioCard()) {
            if (null == currentCard.getAudioCode()) {
                valid = false;
                view.showAudioError(R.string.CARD_EDIT_there_is_no_audio);
                view.showMediaError();
            } else if (!MVPUtils.isYoutubeLink(currentCard.getAudioCode())) {
                valid = false;
                view.showAudioError(R.string.CARD_EDIT_incorrect_audio_code);
                view.showMediaError();
            }
        }


        return valid;
    }

    private void finishWork(Card card) {
         {
            clearEditState();
            view.finishEdit(card);
        }
    }

    private void uploadImage(iStorageSingleton.ImageUploadCallbacks callbacks) {

        byte[] imageBytes = ImageUtils.compressImage(mImageBitmap, mImageType);
        String fileName = ImageUtils.makeFileName(currentCard.getKey(), mImageType);

        view.disableForm();
        view.showImageThrobber();
        view.showProgressMessage(R.string.CARD_EDIT_uploading_image);

        storageSingleton.uploadCardImage(imageBytes, fileName, new iStorageSingleton.FileUploadCallbacks() {
            @Override
            public void onFileUploadProgress(int progress) {

            }

            @Override
            public void onFileUploadSuccess(String fileName, String downloadURL) {
                currentCard.setImageURL(downloadURL);
                currentCard.setFileName(fileName);

                mImageBitmap = null;
                mImageType = null;

                view.hideImageThrobber();
                callbacks.onImageUploaded();
            }

            @Override
            public void onFileUploadFail(String errorMsg) {
                view.hideImageThrobber();
                view.showImageError(R.string.CARD_EDIT_error_saving_image);
                Log.e(TAG, errorMsg);
            }

            @Override
            public void onFileUploadCancel() {
                view.hideImageThrobber();
                view.enableForm();
                view.showToast(R.string.CARD_EDIT_image_upload_cancelled);
            }
        });
    }
}
