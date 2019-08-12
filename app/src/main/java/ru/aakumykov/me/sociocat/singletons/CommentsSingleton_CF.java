package ru.aakumykov.me.sociocat.singletons;

import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.models.Comment;

public class CommentsSingleton_CF implements iCommentsSingleton {

    @Override
    public void loadList(String cardId, @Nullable String startAtKey, @Nullable String endAtKey, ListCallbacks callbacks) {

    }

    @Override
    public void createComment(Comment commentDraft, CreateCallbacks callbacks) {

    }

    @Override
    public void updateComment(Comment comment, CreateCallbacks callbacks) {

    }

    @Override
    public void deleteComment(Comment comment, DeleteCallbacks callbacks) {

    }

    @Override
    public void deleteCommentsForCard(String cardId) throws Exception {

    }

    @Override
    public void rateUp(String commentId, String userId, RatingCallbacks callbacks) {

    }

    @Override
    public void rateDown(String commentId, String userId, RatingCallbacks callbacks) {

    }
}
