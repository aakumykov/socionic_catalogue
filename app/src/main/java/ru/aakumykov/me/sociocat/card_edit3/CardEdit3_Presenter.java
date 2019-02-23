package ru.aakumykov.me.sociocat.card_edit3;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_edit.iCardEdit;
import ru.aakumykov.me.sociocat.interfaces.iAuthSingleton;
import ru.aakumykov.me.sociocat.interfaces.iCardsSingleton;
import ru.aakumykov.me.sociocat.interfaces.iStorageSingleton;
import ru.aakumykov.me.sociocat.interfaces.iTagsSingleton;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.models.Tag;
import ru.aakumykov.me.sociocat.services.AuthSingleton;
import ru.aakumykov.me.sociocat.services.CardsSingleton;
import ru.aakumykov.me.sociocat.services.StorageSingleton;
import ru.aakumykov.me.sociocat.services.TagsSingleton;
import ru.aakumykov.me.sociocat.utils.MVPUtils.MVPUtils;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class CardEdit3_Presenter implements iCardEdit3.Presenter {

    private iCardEdit3.View view;
    private SharedPreferences sharedPreferences;
    private iAuthSingleton authService = AuthSingleton.getInstance();
    private iCardsSingleton cardsService = CardsSingleton.getInstance();
    private iTagsSingleton tagsService = TagsSingleton.getInstance();
    private iStorageSingleton storageService = StorageSingleton.getInstance();
    private Card currentCard;
    private String imageType;

    // Системные методы (условно)
    @Override
    public void linkView(iCardEdit3.View view) {
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

        String action = "" + intent.getAction();

        switch (action) {

            case Constants.ACTION_CREATE:
                startCreateCard(intent);
                break;

            case Constants.ACTION_EDIT:
                startEditCard(intent);
                break;

            case Constants.ACTION_EDIT_RESUME:
                restoreEditState();
                break;

            default:
                throw new IllegalArgumentException("Unknown intent's action: '"+action+"'");
        }


    }

    @Override
    public void loadTagsList(final iCardEdit3.TagsListLoadCallbacks callbacks) {
        tagsService.listTags(new iTagsSingleton.ListCallbacks() {

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
    public void processSelectedImage(int resultCode, @Nullable Intent intent) throws Exception {
        if (Activity.RESULT_OK != resultCode)
            return;

        if (null == intent)
            throw new IllegalArgumentException("Intent is NULL");

        Uri imageURI = intent.getParcelableExtra(Intent.EXTRA_STREAM); // Первый способ получить содержимое
        if (null == imageURI) {
            imageURI = intent.getData(); // Второй способ получить содержимое
            if (null == imageURI) {
                throw new Exception("Where is no image data in intent");
            }
        }

        currentCard.setImageURL(""); // Стираю существующий ImageURL, так как выбрана новая картинка
        currentCard.setLocalImageURI(imageURI);

        imageType = MyUtils.detectImageType(view.getAppContext(), imageURI);
        view.displayImage(imageURI.toString());
    }

    @Override
    public void processVideoLink(String videoString) {
        String videoCode = MVPUtils.extractYoutubeVideoCode(videoString);
        currentCard.setVideoCode(videoCode);
        if (null != view)
            view.displayVideo(videoCode);
    }

    @Override
    public void saveEditState() {
        if (null != view) {
            updateCurrentCardFromView();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Gson gson = new Gson();
            String json = gson.toJson(currentCard);
            editor.putString(Constants.CARD, json);
            editor.apply();
        }
    }

    @Override
    public void restoreEditState() {
        if (sharedPreferences.contains(Constants.CARD)) {
            String json = sharedPreferences.getString(Constants.CARD, "");
            if (!TextUtils.isEmpty(json)) {
                Card card = new Gson().fromJson(json, Card.class);
                if (null != card) {
                    currentCard = card;
                    view.displayCard(card);
                    clearEditState();
                }
            }
        }
    }

    @Override
    public void clearEditState() {
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.remove(Constants.CARD);
//        editor.apply();
        view.clearSharedPrefsData(sharedPreferences, Constants.CARD);
    }

    @Override
    public void saveCard(boolean validateFirst) throws Exception {
        if (validateFirst) {
            if (!formIsValid()) return;
        }

        // Сохраняю картинку, если ещё не сохранена
        if (currentCard.isImageCard() && !currentCard.hasImageURL()) {

            String fileName = currentCard.getKey() + "." + imageType;
            Bitmap imageBitmap = view.getImageBitmap();

            if (null != view) {
                view.disableForm();
                view.showImageProgressBar();
            }

            storageService.uploadImage(imageBitmap, imageType, fileName, new iStorageSingleton.FileUploadCallbacks() {

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
                        saveCard(false);
                    } catch (Exception e) {
                        if (null != view) {
                            view.showErrorMsg(R.string.CARD_EDIT_error_saving_card);
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
                        view.showErrorMsg(R.string.CARD_EDIT_image_upload_cancelled);
                }
            });
        }
        else {

            updateCurrentCardFromView();

            // TODO: нужна проверка на авторизованность!
            currentCard.setUserId(authService.currentUserId());
            currentCard.setUserName(authService.currentUserName());

            if (null != view)
                view.disableForm();

            cardsService.saveCard(currentCard, new iCardsSingleton.SaveCardCallbacks() {
                @Override public void onCardSaveSuccess(Card card) {
                    if (null != view) {
                        clearEditState();
                        view.finishEdit(card);
                    }
                }

                @Override public void onCardSaveError(String message) {
                    if (null != view)
                        view.showErrorMsg(R.string.CARD_EDIT_error_saving_card, message);
                }
            });
        }
    }


    // Внутренние методы
    private void startCreateCard(Intent data) {
        Card card = data.getParcelableExtra(Constants.CARD);
        card.setKey(cardsService.createKey());
        currentCard = card;
        if (null != view)
            view.displayCard(currentCard);
    }

    private void startEditCard(Intent intent) {
        String cardKey = intent.getStringExtra(Constants.CARD_KEY);
        if (null == cardKey)
            throw new IllegalArgumentException("There is no cardKey in Intent");

        if (null != view) {
            view.showProgressBar();

            cardsService.loadCard(cardKey, new iCardsSingleton.LoadCallbacks() {
                @Override
                public void onCardLoadSuccess(Card card) {
                    currentCard = card;
                    view.displayCard(card);
                }

                @Override
                public void onCardLoadFailed(String msg) {
                    if (null != view)
                        view.showErrorMsg(R.string.CARD_EDIT_error_loading_card, msg);
                }
            });
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
                view.showToast(R.string.CARD_EDIT_you_must_select_image, Gravity.CENTER);
                valid = false;
            }

            if (hasLocalImageURI && hasRemoteImageURL) {
                view.showToast(R.string.CARD_EDIT_image_error, Gravity.CENTER);
                valid = false;
            }
        }

        // Видео


        return valid;
    }
}
