package ru.aakumykov.me.mvp.interfaces;

import java.util.List;

import ru.aakumykov.me.mvp.models.Comment;

public interface iCommentsSingleton {

    void loadList(ListCallbacks callbacks);
    void createComment(Comment commentDraft, CreateCallbacks callbacks);
    void updateComment(Comment comment, CreateCallbacks callbacks);
    void deleteComment(Comment comment, DeleteCallbacks callbacks);


    interface ListCallbacks {
        void onListLoadSuccess(List<Comment> list);
        void onListLoadError(String errorMessage);
    }

    interface CreateCallbacks {
        void onCreateSuccess(Comment comment);
        void onCreateError(String message);
    }

    interface DeleteCallbacks {
        void onDeleteSuccess(Comment commentd);
        void onDeleteError(String msg);
    }
}
