package ru.aakumykov.me.mvp.interfaces;

import java.util.List;

import ru.aakumykov.me.mvp.models.Comment;

public interface iCommentsSingleton {

    void loadList(String cardId, ListCallbacks callbacks);
    void createComment(Comment commentDraft, CreateCallbacks callbacks);
    void updateComment(Comment comment, CreateCallbacks callbacks);
    void deleteComment(Comment comment, DeleteCallbacks callbacks);


    interface ListCallbacks {
        void onCommentsLoadSuccess(List<Comment> list);
        void onCommentsLoadError(String errorMessage);
    }

    interface CreateCallbacks {
        void onCommentCreateSuccess(Comment comment);
        void onCommentCreateError(String errorMsg);
    }

    interface DeleteCallbacks {
        void onDeleteSuccess(Comment commentd);
        void onDeleteError(String msg);
    }
}
