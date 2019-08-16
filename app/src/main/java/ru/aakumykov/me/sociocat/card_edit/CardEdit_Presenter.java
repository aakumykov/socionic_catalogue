package ru.aakumykov.me.sociocat.card_edit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import ru.aakumykov.me.sociocat.Config;
import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.models.Tag;
import ru.aakumykov.me.sociocat.singletons.AuthSingleton;
import ru.aakumykov.me.sociocat.singletons.CardsSingleton_CF;
import ru.aakumykov.me.sociocat.singletons.StorageSingleton;
import ru.aakumykov.me.sociocat.singletons.TagsSingleton_CF;
import ru.aakumykov.me.sociocat.singletons.UsersSingleton_CF;
import ru.aakumykov.me.sociocat.singletons.iAuthSingleton;
import ru.aakumykov.me.sociocat.singletons.iCardsSingleton;
import ru.aakumykov.me.sociocat.singletons.iStorageSingleton;
import ru.aakumykov.me.sociocat.singletons.iTagsSingleton;
import ru.aakumykov.me.sociocat.singletons.iUsersSingleton;
import ru.aakumykov.me.sociocat.utils.ImageInfo;
import ru.aakumykov.me.sociocat.utils.MVPUtils.MVPUtils;

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
    private iUsersSingleton usersSingleton = UsersSingleton_CF.getInstance();
//    private iCardsSingleton cardsSingleton = CardsSingleton.getInstance();
    private iCardsSingleton cardsSingleton = CardsSingleton_CF.getInstance();
//    private iTagsSingleton tagsSingleton = TagsSingleton.getInstance();
    private iTagsSingleton tagsSingleton = TagsSingleton_CF.getInstance();
    private iStorageSingleton storageSingleton = StorageSingleton.getInstance();

    private Card currentCard;
    private HashMap<String,Boolean> oldCardTags;
    private String imageType;
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

        if (!AuthSingleton.isLoggedIn()) {
            // TODO: requestLogin + CODE_LOGIN_REQUEST ...
            view.requestLogin(Constants.CODE_LOGIN_REQUEST, intent);
            return;
        }

        Card card = intent.getParcelableExtra(Constants.CARD);

        // TODO: проверять права доступа к карточке

        if (null == card.getKey())
            startCreateCard(card);
        else
            startEditCard(card);
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
    public void processSelectedImage(@Nullable Intent data) throws Exception {
        ImageInfo imageInfo = ImageInfo.getImageInfo(view.getAppContext(), data);
        currentCard.setLocalImageURI(imageInfo.getLocalURI());
        imageType = imageInfo.getType();
        view.displayImage(imageInfo.getLocalURI().toString());
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

            String fileNameWithoutExtension = currentCard.getKey();
            Bitmap imageBitmap = view.getImageBitmap();

            if (null != view)
                view.showImageProgressBar();

            storageSingleton.uploadImage(imageBitmap, imageType, fileNameWithoutExtension, new iStorageSingleton.FileUploadCallbacks() {

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

        /*if (editMode.equals(CardEditMode.CREATE)) {

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
        }*/
    }

    @Override public void onCardSaveError(String message) {
        if (null != view) {
            view.showErrorMsg(R.string.CARD_EDIT_error_saving_card, message);
            view.enableForm();
        }
    }


    // Внутренние методы
    private void startCreateCard(Card card) {
        card.setKey(cardsSingleton.createKey());

        currentCard = card;
        editMode = CardEditMode.CREATE;
        imageType = card.getImageType();

        if (null != view) {
            view.setPageTitle(R.string.CARD_EDIT_create_card_title);
            view.displayCard(currentCard);
        }
    }

    private void startEditCard(Card card) {
        String cardKey = card.getKey();
        if (null == cardKey)
            throw new IllegalArgumentException("cardKey is null");

        editMode = CardEditMode.EDIT;

        if (null != view) {
            view.setPageTitle(R.string.CARD_EDIT_edit_card_title, card.getTitle());
            view.disableForm();
            view.showProgressMessage(R.string.CARD_EDIT_loading_card);
        }

        iCardsSingleton.LoadCallbacks loadCallbacks= new iCardsSingleton.LoadCallbacks() {
            @Override
            public void onCardLoadSuccess(Card card) {
                if (null != card) {
                    currentCard = card;
                    oldCardTags = card.getTagsHash();
                    if (null != view)
                        view.displayCard(card);
                }
                else {
                    if (null != view)
                        view.showErrorMsg(R.string.CARD_EDIT_error_loading_card, "Card is null");
                }
            }

            @Override
            public void onCardLoadFailed(String msg) {
                if (null != view) {
                    view.showErrorMsg(R.string.CARD_EDIT_error_loading_card, msg);
                    view.enableForm();
                }
            }
        };

//        cardsSingleton.loadCard(cardKey, loadCallbacks);

        CardsSingleton_CF.getInstance().loadCard(cardKey, loadCallbacks);
    }

    private void updateCurrentCardFromView(){
        if (null != view) {
            currentCard.setTitle(view.getCardTitle());
            currentCard.setQuote(view.getQuote());
            currentCard.setQuoteSource(view.getQuoteSource());
            currentCard.setDescription(view.getDescription());
            currentCard.setTags(new ArrayList<>(view.getTags().keySet()));
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

        if (quote.length() > Config.LONG_TAG_THRESHOLD) {
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
            if (title.length() < Config.TITLE_MIN_LENGTH) {
                view.showTitleError(R.string.CARD_EDIT_title_too_short);
                valid = false;
            }
            if (title.length() > Config.TITLE_MAX_LENGTH) {
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
                if (quote.length() < Config.QUOTE_MIN_LENGTH) {
                    view.showQuoteError(R.string.CARD_EDIT_quote_too_short);
                    valid = false;
                }
                if (quote.length() > Config.QUOTE_MAX_LENGTH) {
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
            if (description.length() < Config.DESCRIPTION_MIN_LENGTH) {
                view.showDescriptionError(R.string.CARD_EDIT_description_too_short);
                valid = false;
            }
            if (description.length() > Config.DESCRIPTION_MAX_LENGTH) {
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

    private void updateCardTags(final Card card) {

        if (null == card)
            throw new IllegalArgumentException("Card is NULL");

        tagsSingleton.updateCardsInTags(
                card.getKey(),
                oldCardTags,
                card.getTagsHash(),
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
            view.finishEdit(card);
        }
    }


}
