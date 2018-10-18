package ru.aakumykov.me.mvp.card_view;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;

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
    }

    interface Model {
        void loadCard(String key, iCardView.Callbacks callbacks);
        void deleteCard(Card card, final iCardView.Callbacks callbacks) throws Exception;
    }

    interface Callbacks {
        void onLoadSuccess(Card card);
        void onLoadFailed(String msg);
        void onLoadCanceled();
        void onDeleteComplete(@Nullable String msg);
    }
}
