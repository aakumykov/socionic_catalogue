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
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.models.Tag;

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
    public void processTags(String cardKey, @Nullable List<Tag> oldTags, @Nullable List<Tag> newTags, @Nullable UpdateCallbacks callbacks) {

        // Компенсирую NULL-аргументы
        if (null == oldTags) oldTags = new ArrayList<>();
        if (null == newTags) newTags = new ArrayList<>();

        // Получаю набор добавленных к карточке меток
        Set<Tag> oldTagsSet = new HashSet<>(oldTags);
        oldTagsSet.removeAll(new HashSet<>(newTags));

        // Получаю набор удалённых из карточки меток
        Set<Tag> newTagsSet = new HashSet<>(newTags);
        newTagsSet.removeAll(new HashSet<>(oldTags));


        WriteBatch writeBatch = firebaseFirestore.batch();

        // Добавляю id карточки к её новым меткам
        for (String tagName : addedTagsNames) {
            DocumentReference addedTagRef = tagsCollection.document(tagName);

            writeBatch.set(addedTagRef, updates, SetOptions.merge());
        }

        // Удаляю id карточки из меток, которых в карточке больше нет
        Map<String,Object> updates = new HashMap<>();
        updates.put(cardKey, FieldValue.delete());

        for (String tagName : removedTags.keySet()) {
            DocumentReference removedTagRef = tagsCollection.document(tagName);
            writeBatch.update(removedTagRef, updates);
        }


        // Применяю изменения
        // TODO: сделать это в виде транзакции?
        writeBatch.commit()
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
                        String errorMsg = e.getMessage();
                        if (null != callbacks)
                            callbacks.onUpdateFail(errorMsg);
                    }
                });
    }

    private boolean tagIsValid(Tag tag) {
        if (null == tag)
            return false;

        if (null == tag.getName() || null == tag.getKey())
            return false;

        return true;
    }

}
