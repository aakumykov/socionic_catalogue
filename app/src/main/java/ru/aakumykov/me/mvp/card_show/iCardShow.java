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
        void showCommentEditDialog(Comment comment);
        void showCommentDeleteDialog(Comment comment);

        void goEditPage(Card card);
        void goList(String tagFilter);

        void disableCommentForm();
        void enableCommentForm();
        void resetCommentForm();
    }

    interface Presenter {

        void linkView(iCardShow.View view);
        void unlinkView();

        void processInputIntent(@Nullable Intent intent) throws Exception;

        void editCard();
//        void onEditCardConfirmed() throws Exception;

        void deleteCard(Card card);
        void onCardDeleteConfirmed(Card card);

        void onTagClicked(String tagName);


        void loadComments(Card card);
        void postComment(String text);
        void replyToComment(String commentId);

        void editComment(Comment comment);
        void onEditCommentConfirmed(Comment comment) throws Exception;

        void deleteComment(Comment comment);
        void onCommentDeleteConfirmed(Comment comment) throws Exception;
    }
}
