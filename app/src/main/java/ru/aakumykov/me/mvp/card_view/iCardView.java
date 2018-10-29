package ru.aakumykov.me.mvp.card_view;

import android.net.Uri;

import java.util.HashMap;
import java.util.List;

import ru.aakumykov.me.mvp.iBaseView;
import ru.aakumykov.me.mvp.interfaces.iAuthService;
import ru.aakumykov.me.mvp.interfaces.iCardsService;
import ru.aakumykov.me.mvp.models.Card;

public interface iCardView {

    interface View extends iBaseView {
        void showWaitScreen();

        void displayCard(Card card);
        void displayImage(Uri imageURI);
        void displayImageError();

        void showTags(HashMap<String,Boolean> tagsHash);

        void showProgressMessage(int messageId); // убрать в BaseView
        void hideProgressMessage(); // убрать в BaseView

        void goEditPage(Card card);
        void goList(String tagFilter);

        void showDeleteDialog();
    }

    interface Presenter {
        void cardKeyRecieved(String key);

        void onTagClicked(String tagName);

        void onEditButtonClicked();
        void onDeleteButtonClicked();
        void onDeleteConfirmed();

        void linkView(iCardView.View view);
        void unlinkView();

        void linkModel(iCardsService model);
        void unlinkModel();

        void linkAuth(iAuthService authService);
        void unlinkAuth();
    }
}
