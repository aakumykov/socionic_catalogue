package ru.aakumykov.me.mvp.card_edit;

import android.arch.lifecycle.MutableLiveData;
import android.net.Uri;

import ru.aakumykov.me.mvp.models.Card;

public interface iCardEdit {

    interface View {
        void fillEditForm(Card card);
        void displayImage(Uri imageUI);

        void showInfo(int msgId);
        void showError(int msgId);
        void hideMessage();

        void enableEditForm();
        void disableEditForm();

        void showProgressBar();
        void hideProgressBar();
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
