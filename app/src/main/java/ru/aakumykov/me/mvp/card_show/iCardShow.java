package ru.aakumykov.me.mvp.card_show;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.List;

import ru.aakumykov.me.mvp.iBaseView;
import ru.aakumykov.me.mvp.interfaces.iAuthSingleton;
import ru.aakumykov.me.mvp.interfaces.iCardsSingleton;
import ru.aakumykov.me.mvp.models.Card;
import ru.aakumykov.me.mvp.models.Comment;

public interface iCardShow {

    interface View extends iBaseView {

        void displayCard(Card card);
        void displayImage(Uri imageURI);
        void displayImageError();

        void showTags(HashMap<String,Boolean> tagsHash);

        void goEditPage(Card card);
        void goList(String tagFilter);

        void showDeleteDialog();

        void disableCommentForm();
        void enableCommentForm();
        void resetCommentForm();

        void attachComments(List<Comment> list);
    }

    interface Presenter {
        void processInputIntent(@Nullable Intent intent) throws Exception;

        void onTagClicked(String tagName);

        void onEditButtonClicked();
        void onDeleteButtonClicked();
        void onDeleteConfirmed();

        void addComment(String text);

        void linkView(iCardShow.View view);
        void unlinkView();
    }
}
