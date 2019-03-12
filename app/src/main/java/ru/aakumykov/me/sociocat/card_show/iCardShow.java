package ru.aakumykov.me.sociocat.card_show;

import android.content.Intent;
import android.net.Uri;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.List;

import ru.aakumykov.me.sociocat.interfaces.iBaseView;
import ru.aakumykov.me.sociocat.interfaces.iCommentsSingleton;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.models.Comment;

public interface iCardShow {

    interface View extends iBaseView {

        void displayCard(Card card);
        void displayImage(Uri imageURI);
        void displayImageError();
        void displayTags(HashMap<String,Boolean> tagsHash);

        void showCommentsThrobber();
        void hideCommentsThrobber();

        void displayComments(List<Comment> list);
        void appendComment(Comment comment);
        void removeComment(Comment comment);

        void disableCommentForm();
        void enableCommentForm();
        void resetCommentForm();

        void showCommentInProgress();
        void hideCommentInProgress();

        void goEditPage(Card card); // не нужно?
        void goList(String tagFilter);

        void showCardRatingThrobber();
        void showCardRating(int ratingValue);

        void onCardRatedUp(int newRating);
        void onCardRatedDown(int newRating);
        void onCardRateError();

        void finishAfterCardDeleting(Card card);
    }

    interface Presenter {

        void linkView(iCardShow.View view);
        void unlinkView();

        void processInputIntent(@Nullable Intent intent) throws Exception;
        void loadComments(Card card);

        void postComment(String text);
        void postCommentReply(String replyText, Comment parentComment);

        void onTagClicked(String tagName);

        void cardDeleteConfirmed(Card card);
        void editCommentConfirmed(Comment comment);
        void deleteCommentConfirmed(Comment comment);

        void rateCardUp();
        void rateCardDown();

        void rateCommentUp(Comment comment, iCommentsSingleton.RatingCallbacks callbacks);
        void rateCommentDown(Comment comment, iCommentsSingleton.RatingCallbacks callbacks);
    }
}
