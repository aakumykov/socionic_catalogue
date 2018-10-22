package ru.aakumykov.me.mvp.card_edit;

import android.arch.lifecycle.MutableLiveData;
import android.net.Uri;

import ru.aakumykov.me.mvp.interfaces.MyInterfaces;
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
        void displayBrokenImage();

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

        void showInfoMsg(int msgId);
        void showErrorMsg(int msgId);
        void showErrorMsg(String msg);
        void hideMsg();

        void displayNewCard(Card card);

        void finishEdit(Card card);
    }

    interface Presenter {
        void linkView(iCardEdit.View view);
        void unlinkView();

        void linkModel(MyInterfaces.CardsService model);
        void unlinkModel();

        void onCreateCard(String cardType);
        void onCardRecieved(Card card);

        void onSaveButtonClicked();
        void onCancelButtonClicked();
        void onImageDiscardClicked();

        void onSelectImageClicked();
        void onImageSelected(Uri imageURI, String mimeType);
    }

}
