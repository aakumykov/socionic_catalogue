package ru.aakumykov.me.mvp.card_show;

import android.net.Uri;

import java.util.HashMap;

import ru.aakumykov.me.mvp.iBaseView;
import ru.aakumykov.me.mvp.interfaces.iAuthSingleton;
import ru.aakumykov.me.mvp.interfaces.iCardsSingleton;
import ru.aakumykov.me.mvp.models.Card;

public interface iCardShow {

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

        void linkView(iCardShow.View view);
        void unlinkView();

        void linkCardsService(iCardsSingleton model);
        void unlinkCardsService();

        void linkAuth(iAuthSingleton authService);
        void unlinkAuthService();
    }
}
