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
        void displayTags(HashMap<String,Boolean> tagsHash);
        void displayComments(List<Comment> list);
        void appendComment(Comment comment);
        void removeComment(Comment comment);

        void showCommentsThrobber();
        void hideCommentsThrobber();

        void showCardDeleteDialog();
        void showCommentDeleteDialog(Comment comment);

        void goEditPage(Card card);
        void goList(String tagFilter);

        void disableCommentForm();
        void enableCommentForm();
        void resetCommentForm();
    }

    interface Presenter {
        void processInputIntent(@Nullable Intent intent) throws Exception;

        void onTagClicked(String tagName);

        void onEditButtonClicked();
        void onDeleteButtonClicked();
        void onCardDeleteConfirmed();
        void onCommentDeleteConfirmed(Comment comment) throws Exception;

        void loadComments(Card card);
        void postComment(String text);
        void replyToComment(String commentId);

        void linkView(iCardShow.View view);
        void unlinkView();

        void deleteComment(Comment comment);
    }
}
