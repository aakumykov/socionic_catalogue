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
        iStorageSingleton.FileUploadCallbacks
{
    private final static String TAG = "CardEdit2_Presenter";
    private iCardEdit2.View view;
    private iCardsSingleton cardsService = CardsSingleton.getInstance();
    private iAuthSingleton authService = AuthSingleton.getInstance();
    private iStorageSingleton storageService = StorageSingleton.getInstance();

    private Card originalCard;
    private Card currentCard;
    private String selectedFileType;
    private Uri selectedImageURI;

    // Интерфейсные методы
    @Override
    public void processInputIntent(Intent intent) throws Exception {
        view.showProgressBar();

        if (null == intent) {
            throw new IllegalArgumentException("Intent is null");
        }

        String action = intent.getAction();
        switch (action+"") {

            case Constants.ACTION_CREATE:
                createCard(intent);
                break;

            case Constants.ACTION_EDIT:
                editCard(intent);
                break;

            case Intent.ACTION_SEND:
                recieveData(intent);
                break;

            default:
                throw new IllegalArgumentException("Unknown action '"+action+"'");
        }


    }

    @Override
    public void processInputImage(Intent data) {
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

        selectedImageURI = imageURI;
        view.displayImage(imageURI);
    }

    @Override
    public void saveCard() throws Exception {

        currentCard.setTitle(view.getCardTitle());
        currentCard.setQuote(view.getCardQuote());
        currentCard.setImageURL(view.getCardImageURL());
        currentCard.setDescription(view.getCardDescription());

        if (null != selectedImageURI) {

            String remoteFileName = constructRemoteFileName();
            if (null == remoteFileName) {
                throw new Exception("Error constructing remote file Name");
            }

            storageService.uploadImage(selectedImageURI, remoteFileName, this);
        }
        else {

        }
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

        originalCard = card; // Клонировать?
        currentCard = card;
    }

    @Override
    public void onLoadFailed(String msg) {
        view.hideProgressBar();
        view.showErrorMsg(R.string.CARD_EDIT_error_loading_card);
    }

    // --Отправки изображения
    @Override
    public void onUploadProgress(int progress) {

    }

    @Override
    public void onUploadSuccess(String downloadURL) {

    }

    @Override
    public void onUploadFail(String errorMsg) {

    }

    @Override
    public void onUploadCancel() {

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

        selectedFileType = mimeType;

        if (mimeType.startsWith("image/")) {
            processInputImage(intent);
        }
        else if (mimeType.startsWith("text/plain")) {
            procesRecievedText(intent);
        }
        else {
            view.showErrorMsg(R.string.CARD_EDIT_unsupported_data_type, "Unsupported data type '"+mimeType+"'");
        }
    }

    private void procesRecievedText(Intent data) {
        if (null == data) {
            view.hideProgressBar();
            view.showErrorMsg(R.string.CARD_EDIT_error_recieving_data, "Intent data is null");
            return;
        }

        String text = data.getStringExtra(Intent.EXTRA_TEXT);

        view.hideProgressBar();
        view.displayQuote(text);
    }

    private String constructRemoteFileName() {

        String cardKey = currentCard.getKey();
        if (null == cardKey) {
            Log.e(TAG, "currentCard key is null");
            return null;
        }

        String fext = MyUtils.mime2ext(selectedFileType);
        if (null == fext) {
            Log.e(TAG, "File extention from selectedFileType is null");
            return null;
        }

        return cardKey + "." + fext;
    }

    //    private void forgetCardData() {
//        Log.d(TAG, "forgetCardData()");
//        currentCard = null;
//        localImageURI = null;
//        localImageType = null;
//        selectedImageURI = null;
//    }
}
