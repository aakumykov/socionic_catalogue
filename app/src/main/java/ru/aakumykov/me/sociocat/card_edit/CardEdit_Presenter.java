package ru.aakumykov.me.sociocat.card_edit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import androidx.annotation.Nullable;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.singletons.iAuthSingleton;
import ru.aakumykov.me.sociocat.singletons.iCardsSingleton;
import ru.aakumykov.me.sociocat.singletons.iStorageSingleton;
import ru.aakumykov.me.sociocat.singletons.iTagsSingleton;
import ru.aakumykov.me.sociocat.singletons.iUsersSingleton;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.models.Tag;
import ru.aakumykov.me.sociocat.singletons.AuthSingleton;
import ru.aakumykov.me.sociocat.singletons.CardsSingleton;
import ru.aakumykov.me.sociocat.singletons.StorageSingleton;
import ru.aakumykov.me.sociocat.singletons.TagsSingleton;
import ru.aakumykov.me.sociocat.singletons.UsersSingleton;
import ru.aakumykov.me.sociocat.utils.MVPUtils.MVPUtils;
import ru.aakumykov.me.sociocat.utils.MyUtils;

import static ru.aakumykov.me.sociocat.Constants.VIDEO_CARD;

public class CardEdit_Presenter implements
        iCardEdit.Presenter,
        iCardsSingleton.SaveCardCallbacks
{
    public enum CardEditMode {
        CREATE,
        EDIT
    }

    private static final String TAG = "CardEdit_Presenter";
    private iCardEdit.View view;
    private SharedPreferences sharedPreferences;

    private iAuthSingleton authSingleton = AuthSingleton.getInstance();
    private iUsersSingleton usersSingleton = UsersSingleton.getInstance();
    private iCardsSingleton cardsSingleton = CardsSingleton.getInstance();
    private iTagsSingleton tagsSingleton = TagsSingleton.getInstance();
    private iStorageSingleton storageSingleton = StorageSingleton.getInstance();

    private Card currentCard;
    private HashMap<String,Boolean> oldCardTags;
    private String imageType;
    private boolean isExternalDataMode = false;
    private CardEditMode editMode;


    // Системные методы (условно)
    @Override
    public void linkView(iCardEdit.View view) {
        this.view = view;
        sharedPreferences = view.getSharedPrefs(Constants.SHARED_PREFERENCES_CARD_EDIT);
    }

    @Override
    public void unlinkView() {
        this.view = null;
    }


    // Интерфейсные методы
    @Override
    public void processInputIntent(@Nullable Intent intent) throws Exception {

        if (null == intent)
            throw new IllegalArgumentException("Intent is NULL");

//        if (!AuthSingleton.isLoggedIn()) {
//            Bundle bundle = new Bundle();
//                bundle.putParcelable(Intent.EXTRA_INTENT, intent);
//            view.requestLogin(Constants.CODE_EDIT_CARD, bundle);
//        }

        String action = intent.getAction()+"";

        switch (action) {

            case "CREATE_TEXT_CARD":
                startCreateCard(Constants.TEXT_CARD);
                break;

            case "CREATE_IMAGE_CARD":
                startCreateCard(Constants.IMAGE_CARD);
                break;

            case "CREATE_VIDEO_CARD":
                startCreateCard(Constants.VIDEO_CARD);
                break;

            case "CREATE_AUDIO_CARD":
                startCreateCard(Constants.AUDIO_CARD);
                break;

            case Constants.ACTION_CREATE:
                startCreateCard(intent);
                break;

            case Constants.ACTION_EDIT:
                startEditCard(intent);
                break;

            case Constants.ACTION_EDIT_RESUME:
                restoreEditState();
                break;

            case Intent.ACTION_SEND:
                prepareCardCreation();
                processRecievedData(intent);
                break;

            default:
                throw new IllegalArgumentException("Unknown intent's action: '"+action+"'");
        }


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
    public void removeMedia() {
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
    public void processIncomingImage(@Nullable Intent intent) throws Exception {

        if (null == intent)
            throw new Exception("Intent is null");

        Object imageURI = intent.getParcelableExtra(Intent.EXTRA_STREAM); // Первый способ получить содержимое

        if (null == imageURI) {
            imageURI = intent.getData(); // Второй способ получить содержимое

            if (null == imageURI) {
                imageURI = intent.getStringExtra(Intent.EXTRA_TEXT); // Третий способ

                if (null == imageURI) {
                    throw new Exception("Where is no image data in intent");
                }
            }
        }

        if (imageURI instanceof Uri) {
            imageType = MyUtils.detectImageType(view.getAppContext(), (Uri) imageURI);
            currentCard.setLocalImageURI((Uri) imageURI);
        }
        else if (imageURI instanceof String) {
            imageType = MyUtils.detectImageType(view.getAppContext(), (String) imageURI);
            currentCard.setLocalImageURI((String) imageURI);
        }
        else
            throw new Exception("Wring type of imageURI variable");

        currentCard.setImageURL("");
        view.displayImage(imageURI.toString());
    }

    @Override
    public void processYoutubeLink(String youtubeLink) throws Exception {
        String youtubeCode = MVPUtils.extractYoutubeVideoCode(youtubeLink);

        if (null != youtubeCode) {
            if (currentCard.isVideoCard()) {
                currentCard.setVideoCode(youtubeCode);
                view.displayVideo(youtubeCode);
            } else {
                currentCard.setAudioCode(youtubeCode);
                view.displayAudio(youtubeCode);
            }
        } else {
            view.showErrorMsg(R.string.CARD_EDIT_error_adding_video, "Wrong video code: "+ youtubeLink);
        }
    }

    @Override
    public void saveEditState() {
        updateCurrentCardFromView();

        MVPUtils.saveCardDraft(view.getAppContext(), currentCard);

        view.showToast(R.string.CARD_EDIT_draft_saved);

//        SharedPreferences.Editor editor = sharedPreferences.edit();
//
//        String cardJson = new Gson().toJson(currentCard);
//
//        editor.putString(Constants.CARD, cardJson);
//        editor.putString("editMode", editMode.name());
//        editor.putBoolean("isExternalDataMode", isExternalDataMode);
//        editor.putString("imageType", imageType);
//
//        /* У объекта oldCardTags запускается метод, поэтому
//        нужно проверять его существование. */
//        if (null != oldCardTags)
//            editor.putStringSet("oldCardTags", oldCardTags.keySet());
//
//        editor.putString("videoCode", currentCard.getVideoCode());
//        editor.putString("audioCode", currentCard.getAudioCode());
//
//        editor.apply();
    }

    @Override
    public void restoreEditState() throws Exception {

        Card cardDraft = MVPUtils.retriveCardDraft(view.getAppContext());
        if (null != cardDraft)
            view.showDraftRestoreDialog(cardDraft);
    }

    @Override
    public void clearEditState() {
        //view.clearSharedPrefs(sharedPreferences, Constants.CARD);
        MVPUtils.clearCardDraft(view.getAppContext());
    }

    @Override
    public void saveCard(boolean alreadyValidated) throws Exception {

        updateCurrentCardFromView();

        if (!alreadyValidated) {

            processBeforeSave();

            if (!formIsValid()) {
                view.showToast(R.string.CARD_EDIT_form_filling_error);
                return;
            }
        }

        if (null != view)
            view.disableForm();

        // Сохраняю картинку, если этого ещё не сделано
        if (currentCard.isImageCard() && !currentCard.hasImageURL()) {

            String fileName = currentCard.getKey() + "." + imageType;
            Bitmap imageBitmap = view.getImageBitmap();

            if (null != view)
                view.showImageProgressBar();

            storageSingleton.uploadImage(imageBitmap, imageType, fileName, new iStorageSingleton.FileUploadCallbacks() {

                @Override public void onFileUploadProgress(int progress) {

                }

                @Override public void onFileUploadSuccess(String fileName, String downloadURL) {
                    if (null != view) {
                        view.hideImageProgressBar();
                        view.displayImage(downloadURL);
                    }

                    currentCard.setFileName(fileName);
                    currentCard.setImageURL(downloadURL);
                    currentCard.clearLocalImageURI();

                    try {
                        saveCard(true);
                    } catch (Exception e) {
                        if (null != view) {
                            view.showErrorMsg(R.string.CARD_EDIT_error_saving_card, e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }

                @Override public void onFileUploadFail(String errorMsg) {
                    view.hideImageProgressBar();
                    if (null != view)
                        view.showErrorMsg(R.string.CARD_EDIT_error_saving_image, errorMsg);
                }

                @Override public void onFileUploadCancel() {
                    view.hideImageProgressBar();
                    if (null != view)
                        view.showErrorMsg(R.string.CARD_EDIT_image_upload_cancelled, "File upload cancelled...");
                }
            });
        }
        else {
            // TODO: нужна проверка на авторизованность!
            currentCard.setUserId(AuthSingleton.currentUserId());
            currentCard.setUserName(usersSingleton.currentUserName());

            // Время создания/правки
            Long currentTime = new Date().getTime();
            switch (editMode) {
                case CREATE:
                    currentCard.setCTime(currentTime);
                    currentCard.setMTime(currentTime);
                    break;
                case EDIT:
                    currentCard.setMTime(currentTime);
                    break;
                default:
                    throw new Exception("Unknown editMode '"+editMode+"'");
            }

            view.showProgressMessage(R.string.CARD_EDIT_saving_card);

            cardsSingleton.saveCard(currentCard, this);
        }
    }


    // Коллбеки
    @Override public void onCardSaveSuccess(Card card) {
        updateCardTags(card);

        if (editMode.equals(CardEditMode.CREATE)) {

            MVPUtils.subscribeToTopicNotifications(
                    view.getAppContext(),
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
        }
    }

    @Override public void onCardSaveError(String message) {
        if (null != view) {
            view.showErrorMsg(R.string.CARD_EDIT_error_saving_card, message);
            view.enableForm();
        }
    }


    // Внутренние методы
    private void startCreateCard(String cardType) {

        Card card = new Card();

        switch (cardType) {
            case Constants.TEXT_CARD:
                card.setType(Constants.TEXT_CARD);
                break;
            case Constants.IMAGE_CARD:
                card.setType(Constants.IMAGE_CARD);
                break;
            case Constants.VIDEO_CARD:
                card.setType(Constants.VIDEO_CARD);
                break;
            case Constants.AUDIO_CARD:
                card.setType(Constants.AUDIO_CARD);
                break;
            default:
                throw new IllegalArgumentException("Unknown card type '"+cardType+"'");
        }

        Intent intent = new Intent();
        intent.putExtra(Constants.CARD, card);
        startCreateCard(intent);
    }

    private void startCreateCard(Intent intent) {
        Card card = intent.getParcelableExtra(Constants.CARD);
        card.setKey(cardsSingleton.createKey());

        editMode = CardEditMode.CREATE;

        currentCard = card;

        view.displayCard(currentCard);
    }

    private void startEditCard(Intent intent) {
        String cardKey = intent.getStringExtra(Constants.CARD_KEY);
        if (null == cardKey)
            throw new IllegalArgumentException("There is no cardKey in Intent");

        editMode = CardEditMode.EDIT;

        if (null != view) {
            view.disableForm();
            view.showProgressMessage(R.string.CARD_EDIT_loading_card);
        }

        cardsSingleton.loadCard(cardKey, new iCardsSingleton.LoadCallbacks() {
            @Override
            public void onCardLoadSuccess(Card card) {
                currentCard = card;
                oldCardTags = card.getTags();
                if (null != view) {
                    view.displayCard(card);
                }
            }

            @Override
            public void onCardLoadFailed(String msg) {
                if (null != view) {
                    view.showErrorMsg(R.string.CARD_EDIT_error_loading_card, msg);
                    view.enableForm();
                }
            }
        });
    }

    private void prepareCardCreation() {
        editMode = CardEditMode.CREATE;
        Card card = new Card();
        card.setKey(cardsSingleton.createKey());
        currentCard = card;
    }

    private void processRecievedData(Intent intent) {

        Intent transitIntent = intent.getParcelableExtra(Intent.EXTRA_INTENT);

        String inputDataMode = MVPUtils.detectInputDataMode(transitIntent);

        try {
            switch (inputDataMode) {

                case Constants.MIME_TYPE_TEXT:
                    currentCard.setType(Constants.TEXT_CARD);
                    procesIncomingText(transitIntent);
                    break;

                case Constants.MIME_TYPE_IMAGE_LINK:
                    currentCard.setType(Constants.IMAGE_CARD);
                    processIncomingImage(transitIntent);
                    break;

                case Constants.MIME_TYPE_IMAGE_DATA:
                    currentCard.setType(Constants.IMAGE_CARD);
                    processIncomingImage(transitIntent);
                    break;

                case Constants.MIME_TYPE_YOUTUBE_VIDEO:
                    currentCard.setType(VIDEO_CARD);
                    processYoutubeVideo(transitIntent);
                    break;

                default:
                    view.showErrorMsg(R.string.CARD_EDIT_unknown_data_mode, "Unknown input data mode: "+inputDataMode);
            }

        }
        catch (Exception e) {
            if (null != view) {
                view.showErrorMsg(R.string.CARD_EDIT_error_processing_data, e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void updateCurrentCardFromView(){
        if (null != view) {
            currentCard.setTitle(view.getCardTitle());
            currentCard.setQuote(view.getQuote());
            currentCard.setQuoteSource(view.getQuoteSource());
            currentCard.setDescription(view.getDescription());
            currentCard.setTags(view.getTags());
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

        view.displayCard(currentCard);

        if (quote.length() > Constants.LONG_TAG_THRESHOLD) {
            String longTag = view.getString(R.string.TAG_long_text);
            currentCard.addTag(longTag);
        }
    }

    private boolean formIsValid() {
        boolean valid = true;

        // Название
        String title = view.getCardTitle().trim();
        if (TextUtils.isEmpty(title)) {
            view.showTitleError(R.string.cannot_be_empty);
            valid = false;
        } else {
            if (title.length() < Constants.TITLE_MIN_LENGTH) {
                view.showTitleError(R.string.CARD_EDIT_title_too_short);
                valid = false;
            }
            if (title.length() > Constants.TITLE_MAX_LENGTH) {
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
                valid = false;
            } else {
                if (quote.length() < Constants.QUOTE_MIN_LENGTH) {
                    view.showQuoteError(R.string.CARD_EDIT_quote_too_short);
                    valid = false;
                }
                if (quote.length() > Constants.QUOTE_MAX_LENGTH) {
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
            valid = false;
        } else {
            if (description.length() < Constants.DESCRIPTION_MIN_LENGTH) {
                view.showDescriptionError(R.string.CARD_EDIT_description_too_short);
                valid = false;
            }
            if (description.length() > Constants.DESCRIPTION_MAX_LENGTH) {
                //int lengthOvershoot = title.length() - Constants.TITLE_MAX_LENGTH;
                view.showDescriptionError(R.string.CARD_EDIT_description_too_long);
                valid = false;
            }
        }

        // Картинка
        if (currentCard.isImageCard()) {
            boolean hasLocalImageURI = (null != currentCard.getLocalImageURI());
            boolean hasRemoteImageURL = currentCard.hasImageURL();

            if (!hasLocalImageURI && !hasRemoteImageURL) {
                view.showToast(R.string.CARD_EDIT_you_must_select_image);
                valid = false;
            }

            if (hasLocalImageURI && hasRemoteImageURL) {
                view.showToast(R.string.CARD_EDIT_image_error);
                valid = false;
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

    private void procesIncomingText(Intent intent) throws Exception {
        String text = intent.getStringExtra(Intent.EXTRA_TEXT);

        if (!TextUtils.isEmpty(text)) {
            String title = MyUtils.cutToLength(text, Constants.TITLE_MAX_LENGTH);
            currentCard.setTitle(title);
            currentCard.setQuote(text);
            if (null != view)
                view.displayCard(currentCard);
        } else {
            throw new IllegalArgumentException("There is no text in Intent.");
        }
    }

    private void processYoutubeVideo(Intent intent) throws Exception {

        String link = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (null == link)
            throw new IllegalArgumentException("Video link is null");

        String videoCode = MVPUtils.extractYoutubeVideoCode(link);
        if (null == videoCode)
            throw new IllegalArgumentException("Where is no video code in link '"+link+"");

        currentCard.setVideoCode(videoCode);
        view.displayVideo(videoCode);
    }

    private void updateCardTags(final Card card) {
        if (null == card)
            throw new IllegalArgumentException("Card is NULL");

        tagsSingleton.updateCardTags(
                card.getKey(),
                oldCardTags,
                card.getTags(),
                new iTagsSingleton.UpdateCallbacks() {
                    @Override
                    public void onUpdateSuccess() {
                        finishWork(card);
                    }

                    @Override
                    public void onUpdateFail(String errorMsg) {
                        if (null != view)
                            view.showErrorMsg(R.string.CARD_EDIT_error_saving_tags, errorMsg);
                        finishWork(card);
                    }
                }
        );
    }

    private void finishWork(Card card) {
        if (null != view) {
            clearEditState();
            if (isExternalDataMode) view.showCard(card);
            else view.finishEdit(card);
        }
    }


}
