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

        void setTitle(String title);
        void setQuote(String quote);
        void loadImage(String quote);
        void setDescription(String description);
    }

    interface Presenter {
        void cardKeyRecieved(String key);
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
