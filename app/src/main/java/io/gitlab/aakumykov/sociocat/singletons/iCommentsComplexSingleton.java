package io.gitlab.aakumykov.sociocat.singletons;

import androidx.annotation.NonNull;

import io.gitlab.aakumykov.sociocat.models.Comment;

public interface iCommentsComplexSingleton {
    void deleteComment(@NonNull Comment comment, CommentDeletionCallbacks callbacks);

    interface CommentDeletionCallbacks {
        void onCommentDeleteSuccess(@NonNull Comment comment);
        void onCommentDeleteError(@NonNull String errorMsg);
    }
}
