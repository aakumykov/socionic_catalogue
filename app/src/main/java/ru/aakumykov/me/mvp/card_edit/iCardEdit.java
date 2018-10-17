package ru.aakumykov.me.mvp.card_edit;

import android.arch.lifecycle.MutableLiveData;
import android.net.Uri;

import ru.aakumykov.me.mvp.models.Card;

public interface iCardEdit {

    interface View {
        void prepareTextCard();
        void prepareImageCard();

        void displayTextCard(Card card);
        void displayImageCard(Card card);

        void displayRemoteImage(String imageURI);
        void displayRemoteImage(Uri imageURI);

        void displayLocalImage(String imageURI);
        void displayLocalImage(Uri imageURI);

        void selectImage();
        void removeImage();

        String getCardTitle();
        String getCardQuote();
        String getCardDescription();

        void showProgressBar();
        void hideProgressBar();

        void showImageProgressBar();

        void enableForm();
        void disableForm();

        void showInfo(int msgId);
        void showError(int msgId);
        void hideMessage();

        void closeActivity(Card card);
        void displayNewCard(Card card);
    }

    interface Presenter {
        void linkView(iCardEdit.View view);
        void unlinkView();

        void onCreateCard(String cardType);
        void onCardRecieved(Card card);

        void onSaveButtonClicked();
        void onCancelButtonClicked();
        void onImageDiscardClicked();

        void onSelectImageClicked();
        void onImageSelected(Uri imageURI, String mimeType);
    }

    interface Model {
        String createKey();
        void uploadImage(Uri imageURI, String imageMimeType, String remotePath, iCardEdit.ModelCallbacks callbacks);
        void cancelImageUpload();
        void saveCard(Card card, iCardEdit.ModelCallbacks callbacks);
    }

    interface ModelCallbacks {
        void onCardSaveSuccess(Card card);
        void onCardSaveError(String message);
        void onCardSaveCancel();

        void onImageUploadProgress(int progress);
        void onImageUploadSuccess(Uri remoteImageURI);
        void onImageUploadError(String message);
        void onImageUploadCancel();
    }
}
