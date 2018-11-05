package ru.aakumykov.me.mvp.card_edit;

import android.content.Intent;
import android.net.Uri;

import java.util.HashMap;

import ru.aakumykov.me.mvp.iBaseView;
import ru.aakumykov.me.mvp.interfaces.iAuthSingleton;
import ru.aakumykov.me.mvp.interfaces.iCardsSingleton;
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

        void prepareTextCardForm(Card cardDraft);
        void prepareImageCardForm(Card cardDraft);

        void addTag(String tagName);
        // void removeTag(tagName|position);

        void selectImage();

        String getCardTitle();
        String getCardQuote();
        String getCardDescription();
        HashMap<String,Boolean> getCardTags();

        String getNewTag();
        void clearNewTag();
        void focusTagInput();

        void enableForm();
        void disableForm();

        void finishEdit(Card card);
    }


    interface Presenter {
        void linkView(iCardEdit.View view);
        void unlinkView();

        void loadCard(final Intent intent) throws Exception;

        void createCard(Card cardDraft);
        void editCard(String cardKey);

        void onSaveButtonClicked();
        void onCancelButtonClicked();
        void onImageDiscardClicked();
        void onAddTagButtonClicked();

        void onSelectImageClicked();
        void onImageSelected(Uri imageURI, String mimeType);
    }

}
