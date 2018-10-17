package ru.aakumykov.me.mvp.card_view;

import android.content.Intent;
import android.support.annotation.Nullable;

import ru.aakumykov.me.mvp.models.Card;

public interface iCardView {

    interface View {
        void showMessage(int msgId, String msgType);
        void hideMessage();

        void showProgressBar();
        void hideProgressBar();

        void showImagePlaceholder();
        void hideImagePlaceholder();

        void showQuote();
        void showImage();
        void showImageIsBroken();

        void displayCard(Card card);

        void setTitle(String title);
        void setQuote(String quote);
        void loadImage(String quote);
        void setDescription(String description);

        void goEditCard(Card card);
        void close();
    }

    interface Presenter {
        void cardKeyRecieved(String key);

        void onEditButtonClicked();
        void onDeleteButtonClicked();

        void activityResultComes(int requestCode, int resultCode, @Nullable Intent data);

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
