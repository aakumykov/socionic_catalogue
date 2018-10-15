package ru.aakumykov.me.mvp.card_edit;

import android.arch.lifecycle.MutableLiveData;
import android.net.Uri;

import ru.aakumykov.me.mvp.models.Card;

public interface iCardEdit {

    interface View {
        void displayTextCard(Card card);
        void displayImageCard(Card card);

        void selectImage();

        void displayImage(String imageURI);
        void displayImage(Uri imageURI);
        void removeImage();

        void showProgressBar();
        void hideProgressBar();

        void enableForm();
        void disableForm();

        void showInfo(int msgId);
        void showError(int msgId);
        void hideMessage();

        void closeActivity();
    }

    interface Presenter {
        void linkView(iCardEdit.View view);
        void unlinkView();

        void onCardRecieved(Card card);
        void saveButonClicked();
        void cancelButtonClicked();
        void selectImageClicked();
        void imageDiscardClicked();
    }

    interface Model {
        void saveCard(Card card, iCardEdit.ModelCallbacks callbacks);
    }

    interface ModelCallbacks {
        void onCardSaveSuccess();
        void onCardSaveError(String message);
        void onCardSaveCancel();

        void onImageUploadSuccess();
        void onImageUploadError(String message);
        void onImageUploadCancel();
    }
}
