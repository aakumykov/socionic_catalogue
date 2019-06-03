package ru.aakumykov.me.sociocat.singletons;

import java.util.List;

import ru.aakumykov.me.sociocat.models.Comment;

public interface iCommentsSingleton {

    void loadList(String cardId, String lastCommentKey, ListCallbacks callbacks);
    void createComment(Comment commentDraft, CreateCallbacks callbacks);
    void updateComment(Comment comment, CreateCallbacks callbacks);
    void deleteComment(Comment comment, DeleteCallbacks callbacks);
    void deleteCommentsForCard(String cardId) throws Exception;
    void rateUp(String commentId, String userId, RatingCallbacks callbacks);
    void rateDown(String commentId, String userId, RatingCallbacks callbacks);

    interface ListCallbacks {
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
}
