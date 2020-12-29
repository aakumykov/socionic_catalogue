package ru.aakumykov.me.sociocat.singletons;

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
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ru.aakumykov.me.sociocat.constants.Constants;
import ru.aakumykov.me.sociocat.models.Tag;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class TagsSingleton implements iTagsSingleton {

    private final static String TAG = "TagsSingleton";
    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private final CollectionReference tagsCollection = firebaseFirestore.collection(Constants.TAGS_PATH);

    /* Одиночка */
    private static volatile TagsSingleton ourInstance;
    public synchronized static TagsSingleton getInstance() {
        synchronized (TagsSingleton.class) {
            if (null == ourInstance) ourInstance = new TagsSingleton();
            return ourInstance;
        }
    }
    private TagsSingleton() {
    }
    /* Одиночка */


    @Override
    public void createTag(Tag tag, TagCallbacks callbacks) {

        String tagKey = tagsCollection.document().getId();
        tag.setKey(tagKey);

        tagsCollection.document(tagKey).set(tag)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callbacks.onTagSuccess(tag);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        callbacks.onTagFail(e.getMessage());
                    }
                });
    }

    @Override
    public void getTag(String key, TagCallbacks callbacks) {
        tagsCollection.document(key).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        try {
                            Tag tag = documentSnapshot.toObject(Tag.class);
                            if (null != tag)
                                callbacks.onTagSuccess(tag);
                            else
                                callbacks.onTagFail("Tag is null");
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                            callbacks.onTagFail(e.getMessage());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        callbacks.onTagFail(e.getMessage());
                    }
                });
    }

    @Override
    public void saveTag(Tag tag, SaveCallbacks callbacks) {
        DocumentReference tagReference;

        if (null == tag.getKey()) {
            tagReference = tagsCollection.document();
            tag.setKey(tagReference.getId());
        }
        else {
            tagReference = tagsCollection.document(tag.getKey());
        }

        tagReference.set(tag)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callbacks.onSaveSuccess(tag);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        callbacks.onSaveFail(e.getMessage());
                    }
                });
    }

    @Override
    public CollectionReference getTagsCollection() {
        return firebaseFirestore.collection(Constants.TAGS_PATH);
    }

    @Override
    public void listTags(ListCallbacks callbacks) {
        tagsCollection.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        boolean error = false;
                        List<Tag> tagsList = new ArrayList<>();

                        try {
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                if (documentSnapshot.exists()) {
                                    Tag tag = documentSnapshot.toObject(Tag.class);
                                    if (tagIsValid(tag)) {
                                        tagsList.add(tag);
                                    }
                                    else {
                                        error = true;
                                        Log.e(TAG, "Error extracting Tag object from DocumentSnapshot: "+documentSnapshot);
                                    }
                                }
                            }
                        }
                        catch (Exception e) {
                            Log.e(TAG, e.getMessage());
                            e.printStackTrace();
                            error = true;
                        }

                        if (error && 0 == tagsList.size())
                            callbacks.onTagsListFail("Error(s) reading tags.");
                        else
                            callbacks.onTagsListSuccess(tagsList);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        callbacks.onTagsListFail(e.getMessage());
                    }
                });
    }

    @Override
    public void processTags(String cardKey, @Nullable List<String> oldTagsNames, @Nullable List<String> newTagsNames, @Nullable UpdateCallbacks callbacks) {

        // Компенсирую NULL-аргументы
        if (null == oldTagsNames) oldTagsNames = new ArrayList<>();
        if (null == newTagsNames) newTagsNames = new ArrayList<>();

        // Определяю, что добавлено, что удалено
        List<String> addedTagsNames = MyUtils.listDiff(newTagsNames, oldTagsNames);
        List<String> removedTagsNames = MyUtils.listDiff(oldTagsNames, newTagsNames);

        firebaseFirestore.runTransaction(new Transaction.Function<Void>() {
            @Nullable @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {

                for (String tagName : removedTagsNames) {
                    DocumentReference tagRef = tagsCollection.document(tagName);
                    transaction.update(
                            tagRef,
                            Tag.KEY_CARDS,
                            FieldValue.arrayRemove(cardKey)
                    );
                }

                for (String tagName : addedTagsNames) {
                    DocumentReference tagRef = tagsCollection.document(tagName);
                    Tag newTag = new Tag(tagName);

                    // Добавляю метку
                    transaction.set(
                            tagRef,
                            newTag,
                            SetOptions.mergeFields(Tag.KEY_NAME, Tag.KEY_KEY)
                    );

                    transaction.update(
                            tagRef,
                            Tag.KEY_CARDS,
                            FieldValue.arrayUnion(cardKey)
                    );
                }

                return null;
            }
        })
        .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if (null != callbacks)
                    callbacks.onUpdateSuccess();
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
                if (null != callbacks)
                    callbacks.onUpdateFail(e.getMessage());
            }
        });
    }

    @Override
    public void processTags(String cardKey, @Nullable HashMap<String, Boolean> oldTags, @Nullable HashMap<String, Boolean> newTags, @Nullable UpdateCallbacks callbacks) {

    }

    @Override
    public void checkTagExists(@Nullable String tagName, ExistanceCallbacks callbacks) {

        if (null == tagName) {
            callbacks.onTagNotExists(tagName);
            return;
        }
        tagsCollection.document(tagName).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        callbacks.onTagExists(tagName);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callbacks.onTagNotExists(tagName);
                    }
                });
    }

    private boolean tagIsValid(Tag tag) {
        if (null == tag)
            return false;

        return null != tag.getName() && null != tag.getKey();
    }

}
