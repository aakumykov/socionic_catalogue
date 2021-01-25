package ru.aakumykov.me.sociocat.singletons;

import androidx.annotation.NonNull;

import ru.aakumykov.me.sociocat.models.Comment;

public interface iCommentsComplexSingleton {
    void deleteComment(@NonNull Comment comment, CommentDeletionCallbacks callbacks);

    interface CommentDeletionCallbacks {
        void onCommentDeleteSuccess(@NonNull Comment comment);
        void onCommentDeleteError(@NonNull String errorMsg);
    }
}
