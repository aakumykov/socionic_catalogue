package ru.aakumykov.me.mvp.card_show;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;

import java.util.HashMap;

import ru.aakumykov.me.mvp.iBaseView;
import ru.aakumykov.me.mvp.interfaces.iAuthSingleton;
import ru.aakumykov.me.mvp.interfaces.iCardsSingleton;
import ru.aakumykov.me.mvp.models.Card;

public interface iCardShow {

    interface View extends iBaseView {

        void displayCard(Card card);
        void displayImage(Uri imageURI);
        void displayImageError();

        void showTags(HashMap<String,Boolean> tagsHash);

        void goEditPage(Card card);
        void goList(String tagFilter);

        void showDeleteDialog();
    }

    interface Presenter {
        void processInputIntent(@Nullable Intent intent) throws Exception;

        void onTagClicked(String tagName);

        void onEditButtonClicked();
        void onDeleteButtonClicked();
        void onDeleteConfirmed();

        void linkView(iCardShow.View view);
        void unlinkView();
    }
}
