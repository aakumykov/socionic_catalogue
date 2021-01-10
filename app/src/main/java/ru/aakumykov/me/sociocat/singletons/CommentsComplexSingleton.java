package ru.aakumykov.me.sociocat.singletons;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.models.Comment;
import ru.aakumykov.me.sociocat.utils.ErrorUtils;

public class CommentsComplexSingleton implements iCommentsComplexSingleton {

    // Одиночечка одинокий,
    private static volatile CommentsComplexSingleton ourInstance;
    public synchronized static CommentsComplexSingleton getInstance() {
        synchronized (CommentsComplexSingleton.class) {
            if (null == ourInstance)
                ourInstance = new CommentsComplexSingleton();
            return ourInstance;
        }
    }
    private CommentsComplexSingleton() {}
    // не горюй, одиночечка, ведь ты Одиночка.

    private FirebaseFirestore mFirebaseFirestore = FirebaseFirestore.getInstance();
    private CommentsSingleton mCommentsSingleton = CommentsSingleton.getInstance();
    private CardsSingleton mCardsSingleton = CardsSingleton.getInstance();
    private UsersSingleton mUsersSingleton = UsersSingleton.getInstance();

    @Override
    public void deleteComment(@NonNull Comment comment, CommentDeletionCallbacks callbacks) {

        if (null == comment) {
            callbacks.onCommentDeleteError("Comment cannot be null");
            return;
        }

        WriteBatch writeBatch = mFirebaseFirestore.batch();

        DocumentReference commentDocumentRef = mCommentsSingleton.getCommentsCollection()
                .document(comment.getKey());

        writeBatch.delete(commentDocumentRef);

        String cardKey = comment.getCardId();
        if (null == cardKey) {
            executeWriteBatch(writeBatch, comment, callbacks);
            return;
        }

        mCardsSingleton.checkCardExists(cardKey, new iCardsSingleton.CardCheckExistingCallbacks() {
            @Override
            public void onCardExists(@NonNull String cardKey) {
                DocumentReference cardRef = mCardsSingleton.getCardsCollection().document(cardKey);
                writeBatch.update(cardRef, Card.KEY_COMMENTS_KEYS, FieldValue.arrayRemove(comment.getKey()));
                executeWriteBatch(writeBatch, comment, callbacks);
            }

            @Override
            public void onCardNotExists(@NonNull String notExistingCardKey) {
                executeWriteBatch(writeBatch, comment, callbacks);
            }
        });
    }

    private void executeWriteBatch(@NonNull WriteBatch writeBatch, @NonNull Comment comment, @NonNull CommentDeletionCallbacks callbacks) {
        writeBatch.commit()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callbacks.onCommentDeleteSuccess(comment);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callbacks.onCommentDeleteError(ErrorUtils.getErrorFromException(e, "Unknown error executing comment deletion batch write."));
                        e.printStackTrace();
                    }
                });
    }
}
