package ru.aakumykov.me.mvp.card_edit;

import android.net.Uri;
import android.util.Log;

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
    private String oldImageURI;
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
//        Log.d(TAG, "onCardRecieved(), "+card);

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
    public void onSaveButtonClicked() {
        Log.d(TAG, "saveButtonClicked()");

        String fname = currentCard.getKey();

        String fext = MyUtils.mime2ext(localImageType);
        if (null == fext) {
            view.showError(R.string.wrong_mime_type);
            return;
        }

        String remoteImagePath = Constants.IMAGES_PATH + "/" + fname + "." + fext;
        Log.d(TAG, "remoteImagePath: " + remoteImagePath);

        if (null != localImageURI) {
            Log.d(TAG, "Отправляю новую картинку");
            view.showImageProgressBar();
            model.uploadImage(localImageURI, localImageType, remoteImagePath, this);
        }
    }

    @Override
    public void onCancelButtonClicked() {
        forgetCardData();
        view.closeActivity();
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
    public void imageDiscardClicked() {
        Log.d(TAG, "imageDiscardClicked()");
        oldImageURI = currentCard.getImageURL();
        view.removeImage();
    }



    @Override
    public void onImageUploadProgress(int progress) {
        Log.d(TAG, "imageUploadProgress: "+progress);
    }

    @Override
    public void onImageUploadSuccess(Uri remoteImageURI) {
        Log.d(TAG, "onImageUploadSuccess(), "+remoteImageURI);

        newImageURI = remoteImageURI.toString();
        view.displayRemoteImage(remoteImageURI);

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



    private void saveCompleteCard() throws Exception {
        Log.d(TAG, "saveCompleteCard()");

        currentCard.setTitle(view.getCardTitle());
        currentCard.setDescription(view.getCardDescription());

        switch (currentCard.getType()) {
            case Constants.TEXT_CARD:
                currentCard.setQuote(view.getCardQuote());
                break;
            case Constants.IMAGE_CARD:
                currentCard.setImageURL(newImageURI);
                break;
            default:
                view.showError(R.string.wrong_card_type);
                throw new Exception("Unknown card type: "+currentCard.getType());
        }

        model.saveCard(currentCard, this);
    }

    @Override
    public void onCardSaveSuccess() {
        forgetCardData();
        // TODO: передавать новую карточку
        view.closeActivity();
    }

    @Override
    public void onCardSaveError(String message) {
        view.showError(R.string.error_saving_card);
        Log.e(TAG, message);
    }

    @Override
    public void onCardSaveCancel() {
        view.showError(R.string.card_saving_cancelled);
    }



    private void forgetCardData() {
        Log.d(TAG, "forgetCardData()");
        currentCard = null;
        localImageURI = null;
        localImageType = null;
        oldImageURI = null;
        newImageURI = null;
    }
}