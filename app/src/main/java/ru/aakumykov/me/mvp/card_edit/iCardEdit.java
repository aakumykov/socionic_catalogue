package ru.aakumykov.me.mvp.card_edit;

import android.net.Uri;

import ru.aakumykov.me.mvp.interfaces.iCardsService;
import ru.aakumykov.me.mvp.models.Card;

public interface iCardEdit {

    interface View {
        void showWating();
        void hideWating();

        void displayTextCard(Card card);
        void displayImageCard(Card card);

        void showImage(String imageURI);
        void showImage(Uri imageURI);
        void showBrokenImage();
        void removeImage();

        void prepareTextCardForm();
        void prepareImageCardForm();

        void selectImage();

        String getCardTitle();
        String getCardQuote();
        String getCardDescription();

        void enableForm();
        void disableForm();

        void showInfoMsg(int msgId);
        void showErrorMsg(int msgId);
        void showErrorMsg(String msg);
        void hideMsg();

        void finishEdit(Card card);
    }

    interface Presenter {
        void linkView(iCardEdit.View view);
        void unlinkView();

        void linkModel(iCardsService model);
        void unlinkModel();

        void createCard(String cardType);
        void editCard(Card card);

        void onSaveButtonClicked();
        void onCancelButtonClicked();
        void onImageDiscardClicked();

        void onSelectImageClicked();
        void onImageSelected(Uri imageURI, String mimeType);
    }

}
