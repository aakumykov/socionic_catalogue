package ru.aakumykov.me.mvp.card_edit;

import android.net.Uri;
import android.util.Log;

import com.google.firebase.storage.UploadTask;

import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.MyUtils;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.models.Card;


public class CardEdit_Presenter extends android.arch.lifecycle.ViewModel
        implements iCardEdit.Presenter, iCardEdit.ModelCallbacks {

    private final static String TAG = "CardEdit_Presenter";
    private iCardEdit.View view;
    private iCardEdit.Model model;

    private Card currentCard;
    private Uri localImageURI;
    private String localImageType;
    private String newImageURI;

    CardEdit_Presenter() {
        Log.d(TAG, "new CardEdit_Presenter()");
        if (null == model) model = CardEdit_Model.getInstance();
    }

    @Override
    public void linkView(iCardEdit.View view) {
        Log.d(TAG, "linkView()");
        if (null == this.view) this.view = view;
    }
    @Override
    public void unlinkView() {
        Log.d(TAG, "unlinkView()");
        this.view = null;
    }


    @Override
    public void onCardRecieved(Card card) {
        Log.d(TAG, "onCardRecieved(), "+card);

        currentCard = card;

        switch (card.getType()) {
            case Constants.TEXT_CARD:
                view.displayTextCard(card);
                break;
            case Constants.IMAGE_CARD:
                view.displayImageCard(card);
                break;
            default:
                view.showError(R.string.wrong_card_type);
                Log.e(TAG, "Unknown card type: "+card.getType());
        }
    }

    @Override
    public void onCreateCard(String cardType) {
        Log.d(TAG, "onCreateCard("+cardType+")");

        // TODO: где будет корректно проверять cardType ?
        switch (cardType) {
            case Constants.TEXT_CARD:
                view.prepareTextCard();
                break;
            case Constants.IMAGE_CARD:
                view.prepareImageCard();
                break;
            default:
                return;
        }

        String newKey = model.createKey();
        Log.d(TAG, "newKey: "+newKey);

        currentCard = new Card();
        currentCard.setKey(newKey);
        currentCard.setType(cardType);
    }

    @Override
    public void onCancelButtonClicked() {
        Log.d(TAG, "onCancelButtonClicked()");
        forgetCardData();
        model.cancelImageUpload();
        view.closeActivity(null);
    }

    @Override
    public void onSelectImageClicked() {
        Log.d(TAG, "onSelectImageClicked()");
        view.selectImage();
    }

    @Override
    public void onImageSelected(Uri imageURI, String mimeType) {
        Log.d(TAG, "onImageSelected("+imageURI+", "+mimeType+")");
        localImageURI = imageURI;
        localImageType = mimeType;
        view.displayLocalImage(imageURI);
    }

    @Override
    public void onImageDiscardClicked() {
        Log.d(TAG, "onImageDiscardClicked()");
        model.cancelImageUpload();
        view.removeImage();
    }


    // TODO: нормальная отмена выгрузки картинки...

    @Override
    public void onSaveButtonClicked() {
        Log.d(TAG, "saveButtonClicked()");

        if (null != localImageURI) {
            Log.d(TAG, "Новая картинка");
            try {
                String remoteImagePath = constructImagePath();
                view.showImageProgressBar();
                view.disableForm();
                model.uploadImage(localImageURI, localImageType, remoteImagePath, this);

            } catch (Exception e) {
                // TODO: сделать возможным это?
                //view.showError(e.getMessage());
                view.showError(R.string.image_upload_error);
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "Старая картинка");
            try {
                saveCompleteCard();
            } catch (Exception e) {
                view.showError(R.string.error_saving_card);
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onImageUploadProgress(int progress) {
        Log.d(TAG, "imageUploadProgress: "+progress);
    }

    @Override
    public void onImageUploadSuccess(Uri remoteImageURI) {
        Log.d(TAG, "onImageUploadSuccess(), "+remoteImageURI);

        newImageURI = remoteImageURI.toString();
//        view.displayRemoteImage(remoteImageURI);

        try {
            saveCompleteCard();
        } catch (Exception e) {
            view.showError(R.string.error_saving_card);
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onImageUploadError(String message) {
        view.showError(R.string.image_upload_error);
    }

    @Override
    public void onImageUploadCancel() {
        view.showError(R.string.image_upload_cancelled);
    }



    // TODO: проверка данных перед отправкой
    private void saveCompleteCard() throws Exception {
        Log.d(TAG, "saveCompleteCard()");

        currentCard.setTitle(view.getCardTitle());
        currentCard.setDescription(view.getCardDescription());

        switch (currentCard.getType()) {
            case Constants.TEXT_CARD:
                currentCard.setQuote(view.getCardQuote());
                break;
            case Constants.IMAGE_CARD:
                if ((null != newImageURI))
                    currentCard.setImageURL(newImageURI);
                break;
            default:
                view.showError(R.string.wrong_card_type);
                throw new Exception("Unknown card type: "+currentCard.getType());
        }

        view.disableForm();
        model.saveCard(currentCard, this);
    }

    @Override
    public void onCardSaveSuccess(Card card) {
        // TODO: передавать новую карточку
        forgetCardData();
//        view.closeActivity(card);
        view.displayNewCard(card);
    }

    @Override
    public void onCardSaveError(String message) {
        view.showError(R.string.error_saving_card);
        view.enableForm();
        Log.e(TAG, message);
    }

    @Override
    public void onCardSaveCancel() {
        view.showError(R.string.card_saving_cancelled);
        view.enableForm();
    }


    private String constructImagePath() throws Exception {
        String fname = currentCard.getKey();
        String fext = MyUtils.mime2ext(localImageType);

        if (null == fext) {
            view.showError(R.string.wrong_mime_type);
            throw new Exception("Wrong mime type: "+localImageType);
        }

        return Constants.IMAGES_PATH + "/" + fname + "." + fext;
    }

    private void forgetCardData() {
        Log.d(TAG, "forgetCardData()");
        currentCard = null;
        localImageURI = null;
        localImageType = null;
        newImageURI = null;
    }

}