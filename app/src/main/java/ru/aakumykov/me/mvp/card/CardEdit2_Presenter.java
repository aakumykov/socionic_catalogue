package ru.aakumykov.me.mvp.card;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
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
    public void makeStartDecision(@Nullable Intent intent) {
        if (null == intent) {
            view.showErrorMsg(R.string.CARD_EDIT_error_no_input_data);
            return;
        }

        String action = intent.getAction();

        switch (action+"") {

            case Constants.ACTION_CREATE:
                try {
                    processCardCreation(true, intent);
                } catch (Exception e) {
                    view.showErrorMsg(R.string.CARD_EDIT_error_creating_card, e.getMessage());
                    e.printStackTrace();
                }
                break;

            case Intent.ACTION_SEND:
                try {
                    processCardCreation(false, intent);
                } catch (Exception e) {
                    view.showErrorMsg(R.string.CARD_EDIT_error_creating_card, e.getMessage());
                    e.printStackTrace();
                }
                break;

            case Constants.ACTION_EDIT:
                try {
                    processCardEdition(intent);
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
    public void processInputIntent(String mode, final Intent intent) {

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
    private void processCardCreation(boolean fromScratch, Intent intent) throws Exception {

        if (!fromScratch) {

            if (null == intent) {
                throw new IllegalArgumentException("Intent is null");
            }

            String mimeType = MyUtils.getMimeTypeFromIntent(intent);
            if (null == mimeType) {
                throw new IllegalArgumentException("Intent's mimeType is null");
            }

            view.setPageTitle(R.string.CARD_EDIT_card_creation_title);

            if (mimeType.startsWith("text/")) {
                procesIncomingText(intent);
            }
            else if (mimeType.startsWith("image/")) {
                procesIncomingImage(intent);
            }
            else {
                throw new IllegalArgumentException("Unsupported data type '"+mimeType+"'");
            }
        }
    }

    private void processCardEdition(Intent intent) {

        String cardKey = intent.getStringExtra(Constants.CARD_KEY);
        if (null == cardKey) {
            throw new IllegalArgumentException("There is no CARD_KEY in intent.");
        }

        cardsService.loadCard(cardKey, this);
    }

    private void procesIncomingText(Intent data) throws Exception {

        String text = data.getStringExtra(Intent.EXTRA_TEXT);
        if (null == text) {
            throw new Exception("Input text (Intent's extra text) is null");
        }

        view.hideProgressBar();
        view.displayQuote(text);
    }

    private void procesIncomingImage(Intent data) throws Exception {

        Uri imageURI = data.getParcelableExtra(Intent.EXTRA_STREAM);
        if (null == imageURI) {
            throw new Exception("Input image (Intent's extra stream) is null");
        }

        view.hideProgressBar();
        view.displayImage(imageURI);
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
