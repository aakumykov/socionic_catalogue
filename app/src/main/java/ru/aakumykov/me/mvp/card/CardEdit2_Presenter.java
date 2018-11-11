package ru.aakumykov.me.mvp.card;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Arrays;

import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.interfaces.iAuthSingleton;
import ru.aakumykov.me.mvp.interfaces.iCardsSingleton;
import ru.aakumykov.me.mvp.interfaces.iStorageSingleton;
import ru.aakumykov.me.mvp.models.Card;
import ru.aakumykov.me.mvp.services.AuthSingleton;
import ru.aakumykov.me.mvp.services.CardsSingleton;
import ru.aakumykov.me.mvp.services.StorageSingleton;
import ru.aakumykov.me.mvp.utils.MyUtils;

public class CardEdit2_Presenter implements
        iCardEdit2.Presenter,
        iCardsSingleton.LoadCallbacks,
        iCardsSingleton.SaveCardCallbacks,
        iStorageSingleton.FileUploadCallbacks
{
    private final static String TAG = "CardEdit2_Presenter";
    private iCardEdit2.View view;
    private iCardsSingleton cardsService = CardsSingleton.getInstance();
    private iAuthSingleton authService = AuthSingleton.getInstance();
    private iStorageSingleton storageService = StorageSingleton.getInstance();

    private Card currentCard = null;
    private boolean externalDataMode = false;


    // Интерфейсные методы
    @Override
    public void makeStartDecision(@Nullable Intent intent) {
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
                    externalDataMode = true;
                    processInputFile(Constants.MODE_SEND, intent);
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
    public void processInputFile(String mode, final Intent intent) throws Exception {

        if (null == intent) {
            throw new IllegalArgumentException("Intent is null");
        }

        // Выделяю внешние данные
        Uri dataURI;
        String mimeType = null;

        if (Constants.MODE_SELECT.equals(mode)) {
            dataURI = intent.getData();
            mimeType = view.detectMimeType(dataURI);
        }
        else if (Constants.MODE_SEND.equals(mode)) {
            dataURI = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
            mimeType = MyUtils.getMimeTypeFromIntent(intent);
        }
        else {
            throw new IllegalArgumentException("Unknown mode '"+mode+"'");
        }

        if (null == mimeType) throw new IllegalArgumentException("mimeType from Intent is null.");
        if (null == dataURI) throw new IllegalArgumentException("Data from Intent is null");

        // Обрабатываю данные согласно типу
        if (mimeType.startsWith("image/")) {
            try {
                procesIncomingImage(dataURI);
            } catch (Exception e) {
                view.showErrorMsg(R.string.CARD_EDIT_error_processing_data, e.getMessage());
                e.printStackTrace();
            }
        }
        else if (mimeType.equals("text/plain")) {
            try {
                procesIncomingText(intent);
            } catch (Exception e) {
                view.showErrorMsg(R.string.CARD_EDIT_error_processing_data, e.getMessage());
                e.printStackTrace();
            }
        }
        else {
            throw new IllegalArgumentException("Unsupported mimeType '"+mimeType+"'");
        }

        // Если это процесс получения внешних данных...
        if (Constants.MODE_SEND.equals(mode)) {
            prepareCardCreation();
        }
    }

    // TODO: как бы проверять полную корректность при сохранении?
    @Override
    public void saveCard() throws Exception {

        view.disableForm();

        currentCard.setTitle(view.getCardTitle());

        // TODO: В самой Card можно просто игнорировать цитату для нетекстовой карты...
        if (Constants.TEXT_CARD.equals(currentCard.getType())) {
            currentCard.setQuote(view.getCardQuote());
        }

        currentCard.setDescription(view.getCardDescription());

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
                Constants.IMAGE_CARD
        };

        if (Arrays.asList(availableCardTypes).contains(cardType)) {
            currentCard.setType(cardType);
        } else {
            throw new IllegalArgumentException("Unknown card type '"+cardType+"'");
        }
    }

    @Override
    public void forgetSelectedFile() {
        currentCard.clearLocalImageURI();
        currentCard.clearMimeType();
    }



    // Обязательные методы
    @Override
    public void linkView(iCardEdit2.View view) {
        this.view = view;
    }

    @Override
    public void unlinkView() {
        this.view = null;
    }


    // Коллбеки
    // --Загрузки карточки
    @Override
    public void onLoadSuccess(final Card card) {
        currentCard = card;
        view.hideProgressBar();
        view.displayCard(card);
    }

    @Override
    public void onLoadFailed(String msg) {
        currentCard = null;
        view.hideProgressBar();
        view.showErrorMsg(R.string.CARD_EDIT_error_loading_card);
    }

    // --Сохранение карточки
    @Override
    public void onCardSaveSuccess(Card card) {
        // TODO: обновить метки
//        if (externalDataMode) {
//            view.goCardShow(card);
//        } else {
//            view.finishEdit(card);
//        }
        view.goCardShow(card);
    }

    @Override
    public void onCardSaveError(String message) {
//        view.enableForm();
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
    private void prepareCardCreation() throws Exception {

        view.setPageTitle(R.string.CARD_EDIT_card_creation_title);

        currentCard = new Card();
        currentCard.setKey(cardsService.createKey());
    }

    private void prepareCardEdition(Intent intent) {

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
            throw new Exception("Input text (Intent's extra text) is null");
        }

        view.hideProgressBar();
        view.displayQuote(text);
    }

    private void procesIncomingImage(Uri imageURI) throws Exception {

        currentCard.setLocalImageURI(imageURI);

        view.hideProgressBar();
        view.displayImage(imageURI);
    }

    private String makeRemoteFileName() {

        String fname = currentCard.getKey();
        if (null == fname) {
            Log.e(TAG, "fname == null");
            return null;
        }

        String fext = MyUtils.mime2ext(currentCard.getMimeType());
        if (null == fext) {
            Log.e(TAG, "fext == null");
            return null;
        }

        return fname + "." + fext;
    }

}
