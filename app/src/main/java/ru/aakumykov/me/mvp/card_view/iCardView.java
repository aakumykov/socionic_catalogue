package ru.aakumykov.me.mvp.card_view;

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

        void displayCard(Card card);

        void setTitle(String title);
        void setQuote(String quote);
        void loadImage(String quote);
        void setDescription(String description);

        void editCard(Card card);
    }

    interface Presenter {
        void cardKeyRecieved(String key);
        void editButtonPressed();
        void updateCurrentCard(Card card); // как-то это криво

        void linkView(iCardView.View view);
        void unlinkView();
    }

    interface Model {
        void loadCard(String key, iCardView.Callbacks callbacks);
    }

    interface Callbacks {
        void onLoadSuccess(Card card);
        void onLoadFailed(String msg);
        void onLoadCanceled();
    }
}
