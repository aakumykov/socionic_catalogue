package ru.aakumykov.me.sociocat.singletons;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.models.Comment;

public class CommentsSingleton_CF implements iCommentsSingleton {

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference commentsCollection = firebaseFirestore.collection(Constants.COMMENTS_PATH);

    /* Одиночка */
    private static volatile CommentsSingleton_CF ourInstance;
    public synchronized static CommentsSingleton_CF getInstance() {
        synchronized (CommentsSingleton_CF.class) {
            if (null == ourInstance) ourInstance = new CommentsSingleton_CF();
            return ourInstance;
        }
    }
    private CommentsSingleton_CF() { }
    /* Одиночка */



    @Override
    public void loadList(String cardId, @Nullable String startAtKey, @Nullable String endAtKey, ListCallbacks callbacks) {
        Query query = commentsCollection
                .whereEqualTo(Constants.COMMENT_KEY_CARD_ID, cardId)
                .orderBy(Constants.COMMENT_KEY_CREATED_AT, Query.Direction.ASCENDING);

        query.get()
            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<Comment> commentsList = new ArrayList<>();
                boolean error = false;

                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                    try {
                        Comment comment = documentSnapshot.toObject(Comment.class);
                        commentsList.add(comment);
                    } catch (Exception e) {
                        error = true;
                    }
                }

                if (0 == commentsList.size() && error) {
                    callbacks.onCommentsLoadError("Error extracting comments.");
                    return;
                }

                callbacks.onCommentsLoadSuccess(commentsList);
            }
        })
            .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
                callbacks.onCommentsLoadError(e.getMessage());
            }
        });
    }

    @Override
    public void createComment(Comment commentDraft, CreateCallbacks callbacks) {
        commentDraft.setKey(commentsCollection.document().getId());
        updateComment(commentDraft, callbacks);
    }

    @Override
    public void updateComment(Comment comment, CreateCallbacks callbacks) {
        commentsCollection
                .document(comment.getKey())
                .set(comment)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callbacks.onCommentSaveSuccess(comment);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        callbacks.onCommentSaveError(e.getMessage());
                    }
                });
    }

    @Override
    public void deleteComment(Comment comment, DeleteCallbacks callbacks) {
        commentsCollection.document(comment.getKey()).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callbacks.onDeleteSuccess(comment);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        callbacks.onDeleteError(e.getMessage());
                    }
                });
    }

    @Override
    public void deleteCommentsForCard(String cardId) throws Exception {

    }

    @Override
    public void rateUp(String commentId, String userId, RatingCallbacks callbacks) {
        throw new RuntimeException("Метод rateUp() не реализован");
    }

    @Override
    public void rateDown(String commentId, String userId, RatingCallbacks callbacks) {
        throw new RuntimeException("Метод rateDown() не реализован");
    }
}
