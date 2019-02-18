package ru.aakumykov.me.sociocat.card_edit3;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.interfaces.iCardsSingleton;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.services.CardsSingleton;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class CardEdit3_Presenter implements iCardEdit3.Presenter {

    private iCardEdit3.View view;
    private SharedPreferences sharedPreferences;
    private iCardsSingleton cardsService = CardsSingleton.getInstance();
    private Card currentCard;


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
        String imageType = MyUtils.detectImageType(view.getAppContext(), imageURI);
        view.showToast(imageType);
        view.displayImage(imageURI.toString(), true);
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
    public void saveCard() {

    }


    // Внутренние методы
    private void startCreateCard(Intent data) {
        // TODO: проверить с NULL
        Card card = data.getParcelableExtra(Constants.CARD);
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

//    private Card getSavedEditState() {
//        SharedPreferences sharedPreferences = view.getSharedPrefs(Constants.SHARED_PREFERENCES_CARD_EDIT);
//
//        if (sharedPreferences.contains(Constants.CARD)) {
//            String json = sharedPreferences.getString(Constants.CARD,"");
//            return new Gson().fromJson(json, Card.class);
//        } else {
//            return null;
//        }
//    }

    private void updateCurrentCardFromView(){
        if (null != view) {
            currentCard.setTitle(view.getCardTitle());
            currentCard.setQuote(view.getQuote());
            currentCard.setQuoteSource(view.getQuoteSource());
            currentCard.setImageURL(view.getImageURL());
            currentCard.setVideoCode(view.getVideoCode());
            currentCard.setDescription(view.getDescription());

            //currentCard.setTags(view.getTags());
        }
    }
}
