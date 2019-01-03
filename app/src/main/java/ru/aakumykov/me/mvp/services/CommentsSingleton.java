package ru.aakumykov.me.mvp.services;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.sql.Ref;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.interfaces.iCommentsSingleton;
import ru.aakumykov.me.mvp.models.Comment;


public class CommentsSingleton implements iCommentsSingleton {

    /* Одиночка */
    private static volatile CommentsSingleton ourInstance;
    public synchronized static CommentsSingleton getInstance() {
        synchronized (CommentsSingleton.class) {
            if (null == ourInstance) ourInstance = new CommentsSingleton();
            return ourInstance;
        }
    }
    private CommentsSingleton() {}
    /* Одиночка */

    // Свойства
    private final static String TAG = "CommentsSingleton";
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference rootRef = firebaseDatabase.getReference().child("/");
    private DatabaseReference commentsRef = rootRef.child(Constants.COMMENTS_PATH);


    // Интерфейсные методы
    @Override
    public void loadList(String cardId, final ListCallbacks callbacks) {

        // TODO: что будет при кривом cardId? Бросать исключние?
        Query query = commentsRef.orderByChild(Comment.key_cardId)
                .equalTo(cardId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                ArrayList<Comment> commentsList = new ArrayList<>();

                for (DataSnapshot commentSnapshot : dataSnapshot.getChildren()) {
                    try {
                        Comment comment = commentSnapshot.getValue(Comment.class);
                        if (comment != null) comment.setKey(commentSnapshot.getKey());
                        commentsList.add(comment);
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                        e.printStackTrace();
                    }
                }

                callbacks.onCommentsLoadSuccess(commentsList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callbacks.onCommentsLoadError(databaseError.getMessage());
                databaseError.toException().printStackTrace();
            }
        });
    }

    @Override
    public void updateComment(Comment comment, CreateCallbacks callbacks) {
        /* Update работает на основе Create, а не наоборот,
        потому что там используется деструктивный метод setValue(). Название это отражает. */
        createComment(comment.getKey(), comment, callbacks);
    }

    @Override
    public void createComment(final Comment commentDraft, final CreateCallbacks callbacks) {
        commentDraft.setKey(commentsRef.push().getKey());
        createComment(commentDraft.getKey(), commentDraft, callbacks);
    }

    @Override
    public void deleteComment(final Comment comment, final DeleteCallbacks callbacks) {

        HashMap<String,Object> updatePool = new HashMap<>();

        String commentPath = Constants.COMMENTS_PATH+"/"+comment.getKey();
        updatePool.put(commentPath, null);

        String commentInCardPath = Constants.CARDS_PATH+"/"+comment.getCardId()+
                "/commentsKeys/"+comment.getKey();
        updatePool.put(commentInCardPath, null);

        rootRef.updateChildren(updatePool)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        callbacks.onDeleteSuccess(comment);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callbacks.onDeleteError(e.getMessage());
                        e.printStackTrace();
                    }
                });
    }

    @Override
    public void deleteCommentsForCard(String cardId) {

        Query commentsQuery = commentsRef
                .orderByChild("cardId")
                .equalTo(cardId);

        commentsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> keysList = new ArrayList<>();

                for (DataSnapshot oneSnapshot : dataSnapshot.getChildren()) {

                    try {
                        String snapshotKey = oneSnapshot.getKey();
                        Comment comment = oneSnapshot.getValue(Comment.class);
                        if (null != comment) {
                            String commentKey = comment.getKey();
                            keysList.add(commentKey);
                        }

                        deleteCommentsForCard_continue(keysList);

                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, databaseError.getMessage());
                databaseError.toException().printStackTrace();
            }
        });
    }

    @Override
    public void rateUp(String commentId, String byUserId, final RatingCallbacks callbacks) {
        changeRating(commentId, byUserId,1, callbacks);
    }


    @Override
    public void rateDown(String commentId, String byUserId, RatingCallbacks callbacks) {
        changeRating(commentId, byUserId, -1, callbacks);
    }




    // Внутренние методы
    private void createComment(String commentKey, final Comment commentDraft, final CreateCallbacks callbacks) {

        HashMap<String,Object> updatePool = new HashMap<>();

        String commentPath = Constants.COMMENTS_PATH+"/"+commentKey;
        updatePool.put(commentPath, commentDraft);

        String commentInsdeCardPath = Constants.CARDS_PATH + "/" + commentDraft.getCardId() +
                "/commentsKeys/" + commentKey;
        updatePool.put(commentInsdeCardPath, true);

        String commentInsideUserPath = Constants.USERS_PATH + "/" + commentDraft.getUserId() +
                "/commentsKeys/" + commentKey;
        updatePool.put(commentInsideUserPath, true);

        rootRef.updateChildren(updatePool)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callbacks.onCommentSaveSuccess(commentDraft);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callbacks.onCommentSaveError(e.getMessage());
                        e.printStackTrace();
                    }
                });
    }

    private void deleteCommentsForCard_continue(final List<String> keysList) {

        HashMap<String,Object> updatePool = new HashMap<>();

        for (String key : keysList)
            updatePool.put(Constants.COMMENTS_PATH+"/"+key, null);

        rootRef.updateChildren(updatePool)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, e.getMessage());
                        e.printStackTrace();
                    }
                });
    }

    // Внутренние методы
    private void changeRating(final String commentId, final String byUserId, final int ratingDifference, final RatingCallbacks callbacks) {

        DatabaseReference theCommentRef = commentsRef.child(commentId);

        theCommentRef.runTransaction(new Transaction.Handler() {

            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                Comment comment = mutableData.getValue(Comment.class);

                if (null == comment) return Transaction.success(mutableData);

                if (ratingDifference > 0) comment.rateUp(byUserId);
                else comment.rateDown(byUserId);

                mutableData.setValue(comment);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

                if (null == databaseError && null != dataSnapshot) {

                    Comment comment = dataSnapshot.getValue(Comment.class);

                    if (null != comment) {
                        if (ratingDifference > 0) callbacks.onRetedUp(comment);
                        else callbacks.onRatedDown(comment);
                    } else {
                        Log.e(TAG, "Card from dataSnapshot is null");
                    }

                } else {
                    String errorMsg = "Unknown error during rating update of card ("+commentId+").";
                    if (null != databaseError) {
                        errorMsg = databaseError.getMessage();
                        databaseError.toException().printStackTrace();
                    }
                    callbacks.onRateFail(errorMsg);
                }
            }
        });
    }
}
