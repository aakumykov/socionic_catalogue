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
        iCardsSingleton.LoadCallbacks
{
    private final static String TAG = "CardEdit2_Presenter";
    private iCardEdit2.View view;
    private iCardsSingleton cardsService = CardsSingleton.getInstance();
    private iAuthSingleton authService = AuthSingleton.getInstance();
    protected iStorageSingleton storageService = StorageSingleton.getInstance();


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
    public void saveCard() throws Exception {

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
    @Override
    public void onLoadSuccess(Card card) {
        view.hideProgressBar();
        view.displayCard(card);
    }

    @Override
    public void onLoadFailed(String msg) {
        view.hideProgressBar();
        view.showErrorMsg(R.string.CARD_EDIT_error_loading_card);
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

        String mimeType = MyUtils.getMimeTypeFromIntent(intent);
        if (null == mimeType) {
            view.showErrorMsg(R.string.CARD_EDIT_error_recieving_data, "No mime type supplied");
            return;
        }

        if (mimeType.startsWith("image/")) {
            processRecievedImage(intent);
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

    @Override
    public void processRecievedImage(Intent data) {
        if (null == data) {
            view.hideProgressBar();
            view.showBrokenImage();
            view.showErrorMsg(R.string.CARD_EDIT_error_receiving_image, "Intent data is null");
            return;
        }

        Uri imageURI = data.getData();
        view.displayImage(imageURI);
    }
}
