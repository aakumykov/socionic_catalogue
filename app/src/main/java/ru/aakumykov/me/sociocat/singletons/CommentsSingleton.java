package ru.aakumykov.me.sociocat.singletons;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.models.Comment;
import ru.aakumykov.me.sociocat.models.User;

public class CommentsSingleton implements iCommentsSingleton {

    private final static String TAG = "CommentsSingleton";

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference commentsCollection = firebaseFirestore.collection(Constants.COMMENTS_PATH);
    private CollectionReference cardsCollection = firebaseFirestore.collection(Constants.CARDS_PATH);
    private CollectionReference usersCollection = firebaseFirestore.collection(Constants.USERS_PATH);

    /* Одиночка */
    private static volatile CommentsSingleton ourInstance;
    public synchronized static CommentsSingleton getInstance() {
        synchronized (CommentsSingleton.class) {
            if (null == ourInstance) ourInstance = new CommentsSingleton();
            return ourInstance;
        }
    }
    private CommentsSingleton() { }
    /* Одиночка */


    @Override
    public void createComment(Comment comment, CreateCallbacks callbacks) {

        comment.setKey(commentsCollection.document().getId());

        String commentKey = comment.getKey();

        WriteBatch writeBatch = firebaseFirestore.batch();

        writeBatch.set(commentsCollection.document(commentKey), comment);
        writeBatch.update(cardsCollection.document(comment.getCardId()), Card.KEY_COMMENTS_KEYS, FieldValue.arrayUnion(commentKey));
        writeBatch.update(usersCollection.document(comment.getUserId()), User.KEY_COMMENTS_KEYS, FieldValue.arrayUnion(commentKey));

        writeBatch.commit()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callbacks.onCommentSaveSuccess(comment);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callbacks.onCommentSaveError(e.getMessage());
                        Log.e(TAG, Arrays.toString(e.getStackTrace()));
                    }
                });
    }

    @Override
    public void updateComment(Comment comment, CreateCallbacks callbacks) {

        Map<String,Object> updatesMap = new HashMap<>();
        updatesMap.put(Comment.KEY_TEXT, comment.getText());
        updatesMap.put(Comment.KEY_EDITED_AT, comment.getEditedAt());

        commentsCollection
                .document(comment.getKey())
                .update(updatesMap)
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

        String commentKey = comment.getKey();

        WriteBatch writeBatch = firebaseFirestore.batch();

        writeBatch.delete(commentsCollection.document(commentKey));
        writeBatch.update(cardsCollection.document(comment.getCardId()), Card.KEY_COMMENTS_KEYS, FieldValue.arrayRemove(commentKey));
        writeBatch.update(usersCollection.document(comment.getUserId()), User.KEY_COMMENTS_KEYS, FieldValue.arrayRemove(commentKey));

        writeBatch.commit()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callbacks.onDeleteSuccess(comment);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callbacks.onDeleteError(e.getMessage());
                        Log.e(TAG, Arrays.toString(e.getStackTrace()));
                    }
                });
    }

    @Override
    public void deleteCommentsForCard(String cardId) throws Exception {

    }

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
    public void rateUp(String commentId, String userId, RatingCallbacks callbacks) {
        throw new RuntimeException("Метод rateUp() не реализован");
    }

    @Override
    public void rateDown(String commentId, String userId, RatingCallbacks callbacks) {
        throw new RuntimeException("Метод rateDown() не реализован");
    }
}
