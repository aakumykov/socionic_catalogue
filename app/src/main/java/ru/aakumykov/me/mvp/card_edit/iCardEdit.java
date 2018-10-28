package ru.aakumykov.me.mvp.card_edit;

import android.net.Uri;

import java.util.HashMap;
import java.util.List;

import ru.aakumykov.me.mvp.iBaseView;
import ru.aakumykov.me.mvp.interfaces.iCardsService;
import ru.aakumykov.me.mvp.models.Card;

public interface iCardEdit {

    interface View extends iBaseView {
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

        void addTag(String tagName);
        // void removeTag(tagName|position);

        void selectImage();

        String getCardTitle();
        String getCardQuote();
        String getCardDescription();
        HashMap<String,Boolean> getCardTags();

        String getNewTag();
        void clearNewTag();

        void enableForm();
        void disableForm();

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
        void onAddTagButtonClicked();

        void onSelectImageClicked();
        void onImageSelected(Uri imageURI, String mimeType);
    }

}
