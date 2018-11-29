package ru.aakumykov.me.mvp.card;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Arrays;
import java.util.HashMap;

import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.interfaces.iAuthSingleton;
import ru.aakumykov.me.mvp.interfaces.iCardsSingleton;
import ru.aakumykov.me.mvp.interfaces.iStorageSingleton;
import ru.aakumykov.me.mvp.interfaces.iTagsSingleton;
import ru.aakumykov.me.mvp.models.Card;
import ru.aakumykov.me.mvp.services.AuthSingleton;
import ru.aakumykov.me.mvp.services.CardsSingleton;
import ru.aakumykov.me.mvp.services.StorageSingleton;
import ru.aakumykov.me.mvp.services.TagsSingleton;
import ru.aakumykov.me.mvp.utils.MVPUtils.MVPUtils;
import ru.aakumykov.me.mvp.utils.MyUtils;

public class CardEdit_Presenter implements
        iCardEdit.Presenter,
        iCardsSingleton.LoadCallbacks,
        iCardsSingleton.SaveCardCallbacks,
        iStorageSingleton.FileUploadCallbacks
{
    private final static String TAG = "CardEdit_Presenter";
    private iCardEdit.View view;
    private iCardsSingleton cardsService = CardsSingleton.getInstance();
    private iAuthSingleton authService = AuthSingleton.getInstance();
    private iStorageSingleton storageService = StorageSingleton.getInstance();
    private iTagsSingleton tagsService = TagsSingleton.getInstance();

    private Card currentCard = null;
    private HashMap<String,Boolean> oldTags = null;
    private HashMap<String,Boolean> newTags = null;


    // Интерфейсные методы
    @Override
    public void chooseStartVariant(@Nullable Intent intent) {
        if (null == intent) {
            view.showErrorMsg(R.string.CARD_EDIT_error_no_input_data);
            return;
        }

        String action = intent.getAction();

        switch (action+"") {

            case Constants.ACTION_CREATE:
                try {
                    prepareCardCreation();
                } catch (Exception e) {
                    view.showErrorMsg(R.string.CARD_EDIT_error_creating_card, e.getMessage());
                    e.printStackTrace();
                }
                break;

            case Intent.ACTION_SEND:
                try {
                    prepareCardCreation();
                    processRecievedData(Constants.MODE_SEND, intent);
                } catch (Exception e) {
                    view.showErrorMsg(R.string.CARD_EDIT_error_creating_card, e.getMessage());
                    e.printStackTrace();
                }
                break;

            case Constants.ACTION_EDIT:
                try {
                    prepareCardEdition(intent);
                } catch (Exception e) {
                    view.showErrorMsg(R.string.CARD_EDIT_error_editing_card, e.getMessage());
                    e.printStackTrace();
                }
                break;

            default:
                view.showErrorMsg(R.string.CARD_EDIT_error_unknown_action);
        }
    }

    @Override
    public void processRecievedData(String mode, Intent intent) throws Exception {

        String inputDataMode = MVPUtils.detectInputDataMode(intent);

        switch (inputDataMode) {
            case "TEXT":
                currentCard.setType(Constants.TEXT_CARD);
                procesIncomingText(intent);
                break;
            case "IMAGE":
                currentCard.setType(Constants.IMAGE_CARD);
                processIncomingImage(intent);
                break;
            case "YOUTUBE_VIDEO":
                currentCard.setType(Constants.VIDEO_CARD);
                processYoutubeVideo(intent);
                break;
            default:
                view.showErrorMsg(R.string.CARD_EDIT_unknown_data_mode);
        }
    }

    // TODO: как бы проверять полную корректность при сохранении?
    @Override
    public void saveCard() throws Exception {

        view.disableForm();
        view.showProgressBar();

        currentCard.setTitle(view.getCardTitle());

        // TODO: В самой Card можно просто игнорировать цитату для нетекстовой карты...
        if (currentCard.getType().equals(Constants.TEXT_CARD)) {
            currentCard.setQuote(view.getCardQuote());
        }

        if (currentCard.getType().equals(Constants.VIDEO_CARD)) {
            String rawVideoCode = view.getCardVideoCode();

            String videoCode = MVPUtils.extractYoutubeVideoCode(rawVideoCode);
            if (null == videoCode) {
                throw new Exception("Invalid video string: '"+rawVideoCode+"'");
            }

            currentCard.setVideoCode(videoCode);
        }

        currentCard.setDescription(view.getCardDescription());

        newTags = view.getCardTags();
        currentCard.setTags(newTags);

        /* Схема работы:
         1) картинка отправляется на сервер;
         2) карточке присваивается серверный адрес картинки;
         3) локальный адрес стирается;
         4) метод "сохранить" вызывается ешё раз. */
        Uri localImageURI = currentCard.getLocalImageURI();

        if (null != currentCard.getLocalImageURI()) {
            Log.d(TAG, "Отправляю картинку");
            view.showImageProgressBar();

            String remoteFilePath = makeRemoteFileName();
            storageService.uploadImage(localImageURI, remoteFilePath, this);
        }
        else {
            Log.d(TAG, "Сохраняю карточку");
            cardsService.updateCard(currentCard, this);
        }
    }

    @Override
    public void setCardType(String cardType) {
        String[] availableCardTypes = {
                Constants.TEXT_CARD,
                Constants.IMAGE_CARD,
                Constants.VIDEO_CARD
        };

        if (Arrays.asList(availableCardTypes).contains(cardType)) {
            currentCard.setType(cardType);
        } else {
            throw new IllegalArgumentException("Unknown card type '"+cardType+"'");
        }
    }

    @Override
    public String processNewTag(String tagName) {

//        if (!TextUtils.isEmpty(tagName)) {
            tagName = MVPUtils.normalizeTag(tagName);
            return tagName;
//            if (!view.getCardTags().containsKey(tagName)) {
//                return tagName;
//            }
//        }
//
//        return null;
    }

    @Override
    public void forgetCurrentData() {
        currentCard.clearLocalImageURI();
        currentCard.clearMimeType();

        currentCard = null;
        newTags = new HashMap<>();
        oldTags = null;
    }


    // Обязательные методы
    @Override
    public void linkView(iCardEdit.View view) {
        this.view = view;
    }

    @Override
    public void unlinkView() {
        this.view = null;
    }


    // Коллбеки
    // --Загрузки карточки
    @Override
    public void onCardLoadSuccess(final Card card) {
        currentCard = card;
        oldTags = card.getTags();

        view.hideProgressBar();
        view.displayCard(card);
    }

    @Override
    public void onCardLoadFailed(String msg) {
        currentCard = null;
        view.hideProgressBar();
        view.showErrorMsg(R.string.CARD_EDIT_error_loading_card);
    }

    // --Сохранение карточки
    @Override
    public void onCardSaveSuccess(Card card) {

        TagsSingleton.getInstance().updateCardTags(
                currentCard.getKey(),
                oldTags,
                newTags,
                null
        );

        forgetCurrentData();

        view.hideProgressBar();
        view.finishEdit(card);
    }

    @Override
    public void onCardSaveError(String message) {
        view.hideProgressBar();
        view.enableForm();
    }

    // --Отправки изображения
    @Override
    public void onUploadProgress(int progress) {
        view.setImageUploadProgress(progress);
    }

    @Override
    public void onUploadSuccess(String downloadURL) {

        currentCard.clearLocalImageURI();
        currentCard.clearMimeType();

        currentCard.setImageURL(downloadURL);

        view.hideImageProgressBar();

        try {
            saveCard();
        } catch (Exception e) {
            view.showErrorMsg(R.string.CARD_EDIT_error_saving_card, e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onUploadFail(String errorMsg) {
//        view.hideImageProgressBar();
        view.enableForm();
    }

    @Override
    public void onUploadCancel() {
//        view.hideImageProgressBar();
//        view.enableForm();
    }


    // Внутренние методы
    private void prepareCardCreation() {

        view.setPageTitle(R.string.CARD_EDIT_card_creation_title);
        view.showModeSwitcher();

        currentCard = new Card();
        currentCard.setKey(cardsService.createKey());
        // TODO: нужно единообразие аутентификационных вещей!
        currentCard.setUserId(authService.currentUserId());
    }

    private void prepareCardEdition(Intent intent) {

        view.showProgressBar();
        view.setPageTitle(R.string.CARD_EDIT_card_edition_title);

        String cardKey = intent.getStringExtra(Constants.CARD_KEY);
        if (null == cardKey) {
            throw new IllegalArgumentException("There is no CARD_KEY in intent.");
        }

        cardsService.loadCard(cardKey, this);
    }

    private void procesIncomingText(Intent intent) throws Exception {

        String text = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (null == text) {
            throw new IllegalArgumentException("Intent.EXTRA_TEXT is null.");
        }

        String autoTitle = MyUtils.cutToLength(text, Constants.TITLE_MAX_LENGTH);

        view.hideProgressBar();
        view.displayTitle(autoTitle);
        view.displayQuote(text);
    }

    private void processIncomingImage(Intent intent) throws Exception {

        Uri imageURI = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (null == imageURI) {
            throw new IllegalArgumentException("Intent.EXTRA_STREAM is null.");
        }

        currentCard.setLocalImageURI(imageURI);

        view.hideProgressBar();
        view.displayImage(imageURI);
    }

    private void processYoutubeVideo(Intent intent) throws Exception {

        String link = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (null == link) {
            throw new IllegalArgumentException("Video link is null");
        }

        String videoCode = MVPUtils.extractYoutubeVideoCode(link);
        if (null == videoCode) {
            throw new IllegalArgumentException("Where is no video code in link '"+link+"");
        }

        view.displayVideo(videoCode);
    }

    private String makeRemoteFileName() throws Exception {

        String fname = currentCard.getKey();
        if (null == fname) {
            throw new Exception("There is no file name.");
        }

        String fext = MyUtils.mime2ext(currentCard.getMimeType());
        if (null == fext) {
            throw new Exception("There is no file extension.");
        }

        return fname + "." + fext;
    }


}
