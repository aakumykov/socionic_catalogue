package ru.aakumykov.me.sociocat.singletons;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.firestore.CollectionReference;

import java.util.List;

import ru.aakumykov.me.sociocat.models.Comment;

public interface iCommentsSingleton {

    enum CommentRatingAction {
        RATE_UP,
        UNRATE_UP,
        RATE_DOWN,
        UNRATE_DOWN,
        NO_RATING
    }

    CollectionReference getCommentsCollection();

    void loadComments(@Nullable Comment startingComment, LoadListCallbacks callbacks);
    void loadCommentsOfUser(@NonNull String userId, @Nullable Comment startingComment, LoadListCallbacks callbacks);
    void loadCommentsForCard(String cardId, @Nullable Comment startAtComment, @Nullable Comment endAtComment, LoadListCallbacks callbacks);

    void loadComment(String commentKey, LoadCommentCallbacks callbacks);

    void createComment(Comment commentDraft, CreateCallbacks callbacks);
    void updateComment(Comment comment, CreateCallbacks callbacks);
    void deleteComment(Comment comment, DeleteCallbacks callbacks);

    void deleteCommentsForCard(String cardId) throws Exception;

    void changeCommentRating(CommentRatingAction commentRatingAction, Comment comment, String userId, ChangeRatingCallbacks callbacks);


    interface LoadListCallbacks {
        void onCommentsLoadSuccess(List<Comment> list);
        void onCommentsLoadError(String errorMessage);
    }

    interface CreateCallbacks {
        void onCommentSaveSuccess(Comment comment);
        void onCommentSaveError(String errorMsg);
    }

    interface DeleteCallbacks {
        void onDeleteSuccess(Comment commentd);
        void onDeleteError(String msg);
    }

    interface RatingCallbacks {
        void onRetedUp(Comment comment);
        void onRatedDown(Comment comment);
        void onRateFail(String errorMsg);
    }

    interface ChangeRatingCallbacks {
        void onRatingChangeComplete(int newRatingValue, @Nullable String errorMsg);
    }

    interface LoadCommentCallbacks {
        void onLoadCommentSuccess(Comment comment);
        void onLoadCommentError(String errorMsg);
    }
}
