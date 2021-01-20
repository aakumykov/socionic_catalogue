package io.gitlab.aakumykov.sociocat.singletons;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.gitlab.aakumykov.sociocat.models.Card;
import io.gitlab.aakumykov.sociocat.models.Comment;
import io.gitlab.aakumykov.sociocat.models.User;
import io.gitlab.aakumykov.sociocat.utils.ErrorUtils;
import io.gitlab.aakumykov.sociocat.utils.SleepingThread;

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

        String commentKey = comment.getKey();
        if (null == comment) {
            callbacks.onCommentDeleteError("Comment key cannot be null");
            return;
        }

        WriteBatch writeBatch = mFirebaseFirestore.batch();

        DocumentReference commentDocumentRef = mCommentsSingleton.getCommentsCollection().document(commentKey);
        writeBatch.delete(commentDocumentRef);

        List<Runnable> checksList = new ArrayList<>();
        Map<String,Boolean> checksMap = new HashMap<>();
        SleepingThread sleepingThread = new SleepingThread(30, new SleepingThread.iSleepingThreadCallbacks() {
            @Override
            public void onSleepingStart() {

            }

            @Override
            public void onSleepingTick(int secondsToWakeUp) {

            }

            @Override
            public void onSleepingEnd() {
                executeWriteBatch(writeBatch, comment, callbacks);
            }

            @Override
            public boolean isReadyToWakeUpNow() {
                List<Boolean> checkResults = new ArrayList<>(checksMap.values());
                return checkResults.size() == checksList.size();
            }

            @Override
            public void onSleepingError(@NonNull String errorMsg) {
                callbacks.onCommentDeleteError(errorMsg);
            }
        });

        String cardKey = comment.getCardId();
        checksList.add(() -> mCardsSingleton.checkCardExists(cardKey, new iCardsSingleton.CardCheckExistingCallbacks() {
            @Override
            public void onCardExists(@NonNull String cardKey1) {
                synchronized (checksMap) {
                    checksMap.put("card", true);
                }
                DocumentReference cardRef = mCardsSingleton.getCardsCollection().document(cardKey1);
                writeBatch.update(cardRef, Card.KEY_COMMENTS_KEYS, FieldValue.arrayRemove(comment.getKey()));
            }

            @Override
            public void onCardNotExists(@NonNull String notExistingCardKey) {
                synchronized (checksMap) {
                    checksMap.put("card", false);
                }
            }
        }));

        String userId = comment.getUserId();
        checksList.add(() -> mUsersSingleton.checkUserExists(userId, new iUsersSingleton.iCheckExistanceCallbacks() {
            @Override
            public void onCheckComplete() {

            }

            @Override
            public void onExists() {
                synchronized (checksMap) {
                    checksMap.put("user", true);
                }
                DocumentReference userRef = mUsersSingleton.getUsersCollection().document(userId);
                writeBatch.update(userRef, User.KEY_COMMENTS_KEYS, FieldValue.arrayRemove(commentKey));
            }

            @Override
            public void onNotExists() {
                synchronized (checksMap) {
                    checksMap.put("user", false);
                }
            }

            @Override
            public void onCheckFail(String errorMsg) {
                sleepingThread.interrupt();
                callbacks.onCommentDeleteError(errorMsg);
            }
        }));

        sleepingThread.start();

        for (Runnable checkRunnable : checksList)
            checkRunnable.run();
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
