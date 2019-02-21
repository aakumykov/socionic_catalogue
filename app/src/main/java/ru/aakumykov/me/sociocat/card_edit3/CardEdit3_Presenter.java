package ru.aakumykov.me.sociocat.card_edit3;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import java.util.Date;

import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.interfaces.iAuthSingleton;
import ru.aakumykov.me.sociocat.interfaces.iCardsSingleton;
import ru.aakumykov.me.sociocat.interfaces.iStorageSingleton;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.services.AuthSingleton;
import ru.aakumykov.me.sociocat.services.CardsSingleton;
import ru.aakumykov.me.sociocat.services.StorageSingleton;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class CardEdit3_Presenter implements iCardEdit3.Presenter {

    private iCardEdit3.View view;
    private SharedPreferences sharedPreferences;
    private iCardsSingleton cardsService = CardsSingleton.getInstance();
    private iStorageSingleton storageService = StorageSingleton.getInstance();
    private iAuthSingleton authService = AuthSingleton.getInstance();
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
                resumeEditCard(intent);
                break;

            default:
                throw new IllegalArgumentException("Unknown intent's action: '"+action+"'");
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
        imageType = MyUtils.detectImageType(view.getAppContext(), imageURI);
        view.displayImage(imageURI.toString());
    }

    @Override
    public void processVideo(String videoString) {

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
                }
            }
        } else {
            Log.d("qwerty", "123");
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
    public void saveCard() throws Exception {
        if (!formIsValid()) return;

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

                    try {
                        saveCard();
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
                    if (null != view)
                        view.finishEdit(card);
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
        // TODO: проверить с NULL
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

    private void resumeEditCard(Intent intent) {
        SharedPreferences sharedPreferences = view.getSharedPrefs(Constants.SHARED_PREFERENCES_CARD_EDIT);
        if (null != view) {
            currentCard = intent.getParcelableExtra(Constants.CARD);
            view.displayCard(currentCard);
        }
    }

    private void updateCurrentCardFromView(){
        if (null != view) {
            currentCard.setTitle(view.getCardTitle());
            currentCard.setQuote(view.getQuote());
            currentCard.setQuoteSource(view.getQuoteSource());
            currentCard.setDescription(view.getDescription());

            //currentCard.setTags(view.getTags());
        }
    }

    private boolean formIsValid() {
        boolean valid = true;

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

        return valid;
    }
}
