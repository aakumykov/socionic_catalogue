package ru.aakumykov.me.mvp.card_edit;

import android.arch.lifecycle.MutableLiveData;
import android.net.Uri;

import ru.aakumykov.me.mvp.models.Card;

public interface iCardEdit {

    interface View {
        void setTitle(String title);
        void setQuote(String quote);
        void setDescription(String description);

        void displayImage(Uri imageUI);
        void removeImage();

        void showProgressBar();
        void hideProgressBar();

        void enableForm();
        void disableForm();

        void showInfo(int msgId);
        void showError(int msgId);
        void hideMessage();
    }

    interface Presenter {
        void linkView(iCardEdit.View view);
        void unlinkView();

        void cardRecieved(Card card);
        void saveButonClicked();
        void cancelButtonClicked();
        void selectImageButtonClicked();
        void imageDiscardButtonClicked();
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
