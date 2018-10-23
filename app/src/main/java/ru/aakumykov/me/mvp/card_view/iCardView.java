package ru.aakumykov.me.mvp.card_view;

import android.net.Uri;

import ru.aakumykov.me.mvp.interfaces.iCardsService;
import ru.aakumykov.me.mvp.models.Card;

public interface iCardView {

    interface View {
        void showWaitScreen();

        void displayCard(Card card);
        void displayImage(Uri imageURI);

        void displayImageError();

        void showInfoMsg(int messageId);
        void showErrorMsg(int messageId);
        void showErrorMsg(String message);
        void hideMsg();

        void showProgressMessage(int messageId);
        void hideProgressMessage();

        void goEditPage(Card card);
        void closePage();

        void showDeleteDialog();
    }

    interface Presenter {
        void cardKeyRecieved(String key);

        void onEditButtonClicked();
        void onDeleteButtonClicked();
        void onDeleteConfirmed();

        void linkView(iCardView.View view);
        void unlinkView();

        void linkModel(iCardsService model);
        void unlinkModel();
    }
}
