package ru.aakumykov.me.mvp.services;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

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
    private DatabaseReference commentsRef = firebaseDatabase.getReference().child(Constants.COMMENTS_PATH);


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

        DatabaseReference thisCommentRef = commentsRef.child(comment.getKey());

        thisCommentRef.removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (null == databaseError) {
                    callbacks.onDeleteSuccess(comment);
                } else {
                    callbacks.onDeleteError(databaseError.getMessage());
                    databaseError.toException().printStackTrace();
                }
            }
        });
    }


    private void createComment(String key, final Comment commentDraft, final CreateCallbacks callbacks) {

        DatabaseReference commentRef = commentsRef.child(commentDraft.getKey());

        commentRef.setValue(commentDraft)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callbacks.onCommentCreateSuccess(commentDraft);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callbacks.onCommentCreateError(e.getMessage());
                        e.printStackTrace();
                    }
                });
    }
}
