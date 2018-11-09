package ru.aakumykov.me.mvp.card;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

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

    private Card currentCard;
    private Uri localImageURI;
    private String inputDataMimeType;


    // Интерфейсные методы
    @Override
    public void processInputIntent(String mode, final Intent intent) throws Exception {

        if (null == intent) {
            throw new IllegalArgumentException("Intent is null");
        }

        String mimeType = MyUtils.getMimeTypeFromIntent(intent);
        Uri dataURI = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);

        if (null == mimeType) {
            throw new IllegalArgumentException("mimeType from Intent is null.");
        }

        if (null == dataURI) {
            throw new IllegalArgumentException("Data from Intent is null");
        }


    }

    @Override
    public void processIncomingData(Intent data) {
        if (null == data) {
            view.hideProgressBar();
            view.showBrokenImage();
            view.showErrorMsg(R.string.CARD_EDIT_error_receiving_image, "Intent data is null");
            return;
        }

        Uri imageURI = data.getData();
        if (null == imageURI) {
            view.hideProgressBar();
            view.showErrorMsg(R.string.CARD_EDIT_error_receiving_image, "imageURI is null");
        }

        localImageURI = imageURI;

        // TODO: исключение?
        inputDataMimeType = MyUtils.getMimeTypeFromIntent(data);

        view.displayImage(imageURI);
    }

    @Override
    public void saveCard() throws Exception {

        currentCard.setTitle(view.getCardTitle());

        // В самой Card можно просто игнорировать цитату для текстовой карты...
        if (Constants.TEXT_CARD.equals(currentCard.getType())) {
            currentCard.setQuote(view.getCardQuote());
        }

        currentCard.setDescription(view.getCardDescription());

        String remoteFilePath = constructRemoteFilePath();

        /* Схема работы:
         1) картинка отправляется на сервер;
         2) карточке присваивается серверный адрес картинки;
         3) локальный адрес стирается;
         4) метод "сохранить" вызывается ешё раз. */
        if (null != localImageURI) {
            Log.d(TAG, "Отправляю картинку");
            view.disableForm();
            view.showImageProgressBar();
            storageService.uploadImage(localImageURI, remoteFilePath, this);
        }
        else {
            Log.d(TAG, "Сохраняю карточку");
            view.disableForm();
            cardsService.updateCard(currentCard, this);
        }
    }

    @Override
    public void forgetSelectedFile() {
        inputDataMimeType = null;
        localImageURI = null;
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
        view.hideProgressBar();
        view.displayCard(card);
        currentCard = card;
    }

    @Override
    public void onLoadFailed(String msg) {
        view.hideProgressBar();
        view.showErrorMsg(R.string.CARD_EDIT_error_loading_card);
    }

    // --Сохранение карточки
    @Override
    public void onCardSaveSuccess(Card card) {
        // TODO: обновить метки
        view.finishEdit(card);
    }

    @Override
    public void onCardSaveError(String message) {
        view.enableForm();
    }

    // --Отправки изображения
    @Override
    public void onUploadProgress(int progress) {
        view.setImageUploadProgress(progress);
    }

    @Override
    public void onUploadSuccess(String downloadURL) {
        currentCard.setImageURL(downloadURL);

        view.hideImageProgressBar();
        forgetSelectedFile();

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
    private void createCard(Intent intent) {
        Log.d(TAG, "createCard()");
    }

    private void editCard(Intent intent) throws Exception {
        Log.d(TAG, "editCard()");

        String cardKey = intent.getStringExtra(Constants.CARD_KEY);

        if (null == cardKey) {
            throw new IllegalArgumentException("There is no CARD_KEY in intent");
        }

        cardsService.loadCard(cardKey, this);
    }

    private void recieveData(Intent intent) {
        Log.d(TAG, "recieveData()");

        Uri uri = intent.getData();

        String mimeType = MyUtils.getMimeTypeFromIntent(intent);
        if (null == mimeType) {
            view.showErrorMsg(R.string.CARD_EDIT_error_recieving_data, "No mime type supplied");
            return;
        }

        if (mimeType.startsWith("image/")) {
            processIncomingData(intent);
        }
        else if (mimeType.startsWith("text/plain")) {
            procesIncomingText(intent);
        }
        else {
            view.showErrorMsg(R.string.CARD_EDIT_unsupported_data_type, "Unsupported data type '"+mimeType+"'");
        }
    }

    private void procesIncomingText(Intent data) {
        if (null == data) {
            view.hideProgressBar();
            view.showErrorMsg(R.string.CARD_EDIT_error_recieving_data, "Intent data is null");
            return;
        }

        String text = data.getStringExtra(Intent.EXTRA_TEXT);

        view.hideProgressBar();
        view.displayQuote(text);
    }

    private String constructRemoteFilePath() {

        String fname = currentCard.getKey();
        if (null == fname) {
            Log.e(TAG, "fname == null");
            return null;
        }

        String fext = MyUtils.mime2ext(inputDataMimeType);
        if (null == fext) {
            Log.e(TAG, "fext == null");
            return null;
        }

        return fname + "." + fext;
    }

}
