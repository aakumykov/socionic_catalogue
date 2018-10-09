package ru.aakumykov.me.mvp.card_view;

import ru.aakumykov.me.mvp.models.Card;

public interface iCardView {

    interface View {
        void showInfo(int msgId);
        void showError(int msgId);
        void hideMessage();

        void showProgressBar();
        void hideProgressBar();
        void setTitle(String title);
        void setQuote(String quote);
        void setDescription(String description);
    }

    interface Presenter {
        void linkView(iCardView.View view);
        void unlinkView();
    }

    interface Model {
        void loadCard(String key);
    }

    interface Callbacks {
        void onLoadSuccess(Card card);
        void onLoadFailed(String msg);
        void onLoadCanceled();
    }
}
