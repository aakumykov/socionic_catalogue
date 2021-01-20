package io.gitlab.aakumykov.sociocat.singletons;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.gitlab.aakumykov.sociocat.AppConfig;
import io.gitlab.aakumykov.sociocat.constants.Constants;
import io.gitlab.aakumykov.sociocat.models.Card;
import io.gitlab.aakumykov.sociocat.models.Comment;
import io.gitlab.aakumykov.sociocat.models.User;
import io.gitlab.aakumykov.sociocat.utils.ErrorUtils;
import io.gitlab.aakumykov.sociocat.utils.MyUtils;

public class CommentsSingleton implements iCommentsSingleton {

    private final static String TAG = "CommentsSingleton";

    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private final CollectionReference commentsCollection = firebaseFirestore.collection(Constants.COMMENTS_PATH);
    private final CollectionReference cardsCollection = firebaseFirestore.collection(Constants.CARDS_PATH);
    private final CollectionReference usersCollection = firebaseFirestore.collection(Constants.USERS_PATH);


    public static iCommentsSingleton.CommentRatingAction determineRatingAction(@Nullable User user, String commentKey) {
        if (null == user) {
            return iCommentsSingleton.CommentRatingAction.NO_RATING;
        }
        else {
            if (user.alreadyRateUpComment(commentKey)) {
                return CommentRatingAction.RATE_UP;
            } else if (user.alreadyRateDownComment(commentKey)) {
                return CommentRatingAction.RATE_DOWN;
            } else {
                return CommentRatingAction.NO_RATING;
            }
        }
    }


    @Override
    public void createComment(Comment comment, CreateCallbacks callbacks) {

        comment.setKey(commentsCollection.document().getId());

        String commentKey = comment.getKey();

        WriteBatch writeBatch = firebaseFirestore.batch();


        // Коллекция комментариев
        writeBatch.set(commentsCollection.document(commentKey), comment);

        // Прописываю в карточку
        writeBatch.update(cardsCollection.document(comment.getCardId()), Card.KEY_COMMENTS_KEYS, FieldValue.arrayUnion(commentKey));

        // Прописываюсь у пользователя
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
        updatesMap.put(Comment.KEY_PARENT_ID, comment.getParentId());
        updatesMap.put(Comment.KEY_PARENT_TEXT, comment.getParentText());

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


        // Удаляю из коллекции коментариев
        writeBatch.delete(commentsCollection.document(commentKey));

        // Удаляю из списка комментариев к карточке
        writeBatch.update(cardsCollection.document(comment.getCardId()), Card.KEY_COMMENTS_KEYS, FieldValue.arrayRemove(commentKey));

        // Удаляю из списка комментариев пользователя
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
        throw new RuntimeException("deleteCommentsForCard() not implemented");
    }

    @Override
    public CollectionReference getCommentsCollection() {
        return firebaseFirestore.collection(Constants.COMMENTS_PATH);
    }

    @Override
    public void loadComments(@Nullable Comment startingComment, LoadListCallbacks callbacks) {
        Query query = commentsCollection;

        query = query.orderBy(Comment.KEY_CREATED_AT, Query.Direction.DESCENDING);

        if (null != startingComment)
            query = query.startAfter(startingComment.getCreatedAt());

        query = query.limit(AppConfig.DEFAULT_COMMENTS_LOAD_COUNT);

        executeQuery(query, callbacks);
    }

    @Override
    public void loadCommentsOfUser(@NonNull String userId, @Nullable Comment startingComment, LoadListCallbacks callbacks) {

        Query query = commentsCollection;

        query = query.whereEqualTo(Comment.KEY_USER_ID, userId);

        query = query.orderBy(Comment.KEY_CREATED_AT, Query.Direction.DESCENDING);

        if (null != startingComment)
            query = query.startAfter(startingComment.getCreatedAt());

        query = query.limit(AppConfig.DEFAULT_COMMENTS_LOAD_COUNT);

        executeQuery(query, callbacks);
    }

    @Override
    public void loadCommentsForCard(String cardId, @Nullable Comment startAfterComment, @Nullable Comment endBeforeComment, LoadListCallbacks callbacks) {
        Query query = commentsCollection
                .whereEqualTo(Constants.COMMENT_KEY_CARD_ID, cardId)
                .orderBy(Comment.KEY_CREATED_AT)
                .limit(AppConfig.DEFAULT_COMMENTS_LOAD_COUNT);

        if (null != startAfterComment)
            query = query.startAfter(startAfterComment.getCreatedAt());

        if (null != endBeforeComment) {
            query = query.endBefore(endBeforeComment.getCreatedAt());
        }

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
    public void loadComment(String commentKey, LoadCommentCallbacks callbacks) {
        commentsCollection.document(commentKey).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        try {
                            Comment comment = documentSnapshot.toObject(Comment.class);
                            callbacks.onLoadCommentSuccess(comment);
                        }
                        catch (Exception e) {
                            callbacks.onLoadCommentError(e.getMessage());
                            MyUtils.printError(TAG, e);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callbacks.onLoadCommentError(e.getMessage());
                        MyUtils.printError(TAG, e);
                    }
                });
    }

    @Override
    public void changeCommentRating(CommentRatingAction commentRatingAction, Comment comment, String userId, ChangeRatingCallbacks callbacks) {
        String commentKey = comment.getKey();
        int oldCommentRating = comment.getRating();

        WriteBatch writeBatch = firebaseFirestore.batch();
        DocumentReference userRef = usersCollection.document(userId);
        DocumentReference commentRef = commentsCollection.document(commentKey);

        switch (commentRatingAction) {
            case RATE_UP:
                writeBatch.update(commentRef, Comment.KEY_RATING, FieldValue.increment(1));
                writeBatch.update(userRef, User.KEY_RATED_UP_COMMENT_KEYS, FieldValue.arrayUnion(commentKey));
                break;
            case UNRATE_UP:
                writeBatch.update(commentRef, Comment.KEY_RATING, FieldValue.increment(-1));
                writeBatch.update(userRef, User.KEY_RATED_UP_COMMENT_KEYS, FieldValue.arrayRemove(commentKey));
                break;
            case RATE_DOWN:
                writeBatch.update(commentRef, Comment.KEY_RATING, FieldValue.increment(-1));
                writeBatch.update(userRef, User.KEY_RATED_DOWN_COMMENT_KEYS, FieldValue.arrayUnion(commentKey));
                break;
            case UNRATE_DOWN:
                writeBatch.update(commentRef, Comment.KEY_RATING, FieldValue.increment(1));
                writeBatch.update(userRef, User.KEY_RATED_DOWN_COMMENT_KEYS, FieldValue.arrayRemove(commentKey));
                break;
            default:
                throw new UnknownCommentRatingActionException("Bad rating action argument: "+commentRatingAction);
        }

        writeBatch.commit()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        loadComment(commentKey, new LoadCommentCallbacks() {
                            @Override
                            public void onLoadCommentSuccess(Comment comment) {
                                callbacks.onRatingChangeComplete(comment.getRating(), null);
                            }

                            @Override
                            public void onLoadCommentError(String errorMsg) {
                                callbacks.onRatingChangeComplete(oldCommentRating, errorMsg);
                            }
                        });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callbacks.onRatingChangeComplete(oldCommentRating, e.getMessage());
                        MyUtils.printError(TAG, e);
                    }
                });
    }



    private void executeQuery(@NonNull Query query, LoadListCallbacks callbacks) {
        query
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        callbacks.onCommentsLoadSuccess(
                                extractCommentsFromQuerySnapshot(queryDocumentSnapshots)
                        );
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callbacks.onCommentsLoadError(ErrorUtils.getErrorFromException(e, "Unknown error loading comments"));
                        e.printStackTrace();
                    }
                });
    }


    private List<Comment> extractCommentsFromQuerySnapshot(@NonNull QuerySnapshot queryDocumentSnapshots) {
        List<Comment> list = new ArrayList<>();
        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
            try {
                Comment comment = documentSnapshot.toObject(Comment.class);
                list.add(comment);
            } catch (Exception e) {
                Log.e(TAG, ErrorUtils.getErrorFromException(e, "Error extracting Comment from DocumentSnapshot"));
                e.printStackTrace();
            }
        }
        return list;
    }


    public static class UnknownCommentRatingActionException extends IllegalArgumentException {
        public UnknownCommentRatingActionException(String message) {
            super(message);
        }
    }


    // Одиночка
    private static volatile CommentsSingleton ourInstance;
    public synchronized static CommentsSingleton getInstance() {
        synchronized (CommentsSingleton.class) {
            if (null == ourInstance) ourInstance = new CommentsSingleton();
            return ourInstance;
        }
    }
    private CommentsSingleton() { }
    // Одиночка

}
