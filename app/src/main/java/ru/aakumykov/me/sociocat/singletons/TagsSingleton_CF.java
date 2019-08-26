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

import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.models.Tag;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class TagsSingleton_CF implements iTagsSingleton {

    private final static String TAG = "TagsSingleton_CF";
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference tagsCollection = firebaseFirestore.collection(Constants.TAGS_PATH);

    /* Одиночка */
    private static volatile TagsSingleton_CF ourInstance;
    public synchronized static TagsSingleton_CF getInstance() {
        synchronized (TagsSingleton_CF.class) {
            if (null == ourInstance) ourInstance = new TagsSingleton_CF();
            return ourInstance;
        }
    }
    private TagsSingleton_CF() {
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
    public void readTag(String key, TagCallbacks callbacks) {
        tagsCollection.document(key).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        try {
                            Tag tag = documentSnapshot.toObject(Tag.class);
                            callbacks.onTagSuccess(tag);
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
    public void deleteTag(Tag tag, DeleteCallbacks callbacks) {
        tagsCollection.document(tag.getKey()).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callbacks.onDeleteSuccess(tag);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        callbacks.onDeleteFail(e.getMessage());
                    }
                });
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
                            Tag.CARDS_KEY,
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
                            SetOptions.mergeFields("name", "key")
                    );

                    transaction.update(
                            tagRef,
                            Tag.CARDS_KEY,
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

    private boolean tagIsValid(Tag tag) {
        if (null == tag)
            return false;

        if (null == tag.getName() || null == tag.getKey())
            return false;

        return true;
    }

}
