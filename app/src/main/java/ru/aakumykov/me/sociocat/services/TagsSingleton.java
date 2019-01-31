package ru.aakumykov.me.sociocat.services;

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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.utils.MyUtils;
import ru.aakumykov.me.sociocat.interfaces.iTagsSingleton;
import ru.aakumykov.me.sociocat.models.Tag;

public class TagsSingleton implements iTagsSingleton {

    /* Одиночка */
    private static volatile TagsSingleton ourInstance;
    public synchronized static TagsSingleton getInstance() {
        synchronized (TagsSingleton.class) {
            if (null == ourInstance) ourInstance = new TagsSingleton();
            return ourInstance;
        }
    }
    private TagsSingleton() {}
    /* Одиночка */

    private final static String TAG = "TagsSingleton";
    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference tagsRef = dbRef.child(Constants.TAGS_PATH);


    @Override
    public void createTag(final Tag tag, final iTagsSingleton.TagCallbacks callbacks) {
        Log.d(TAG, "createTag(), "+tag);

        HashMap<String, Object> updatePool = new HashMap<>();
        updatePool.put(tag.getName(), true);

        tagsRef.updateChildren(updatePool)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callbacks.onTagSuccess(tag);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callbacks.onTagFail(e.getMessage());
                        e.printStackTrace();
                    }
                });
    }


    @Override
    public void readTag(String key, final TagCallbacks callbacks) {
        Log.d(TAG, "readTag('"+key+"')");

        DatabaseReference tagRef = tagsRef.child(key);

        tagRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Tag tag = dataSnapshot.getValue(Tag.class);

                if (null != tag) {
                    // TODO: объединить их, что ли?
                    tag.setKey(dataSnapshot.getKey());
                    tag.setName(dataSnapshot.getKey());
                    callbacks.onTagSuccess(tag);
                } else {
                    callbacks.onTagFail("Tag from dataSnapshot is null");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callbacks.onTagFail(databaseError.getMessage());
                databaseError.toException().printStackTrace();
            }
        });
    }


    @Override
    public void saveTag(final Tag tag, final SaveCallbacks callbacks) {
        Log.d(TAG, "saveTag(), "+tag);

        DatabaseReference tagRef = tagsRef.child(tag.getKey());

        tagRef.updateChildren(tag.toMap())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callbacks.onSaveSuccess(tag);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callbacks.onSaveFail(e.getMessage());
                        e.printStackTrace();
                    }
                });
    }


    @Override
    public void deleteTag(final Tag tag, final DeleteCallbacks callbacks) {
        Log.d(TAG, "deleteTag(), "+tag);

        DatabaseReference tagRef = tagsRef.child(tag.getKey());

        tagRef.removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        callbacks.onDeleteSuccess(tag);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callbacks.onDeleteFail(e.getMessage());
                        e.printStackTrace();
                    }
                });
    }


    // TODO: throw Exception
    @Override
    public void listTags(final ListCallbacks callbacks) {
        Log.d(TAG, "listTags()");

        final List<Tag> list = new ArrayList<>();

        tagsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Log.d(TAG, "dataSnapshot: "+dataSnapshot);

                for (DataSnapshot tagSnapshot : dataSnapshot.getChildren()) {
//                    Log.d(TAG, "tagSnapshot: "+tagSnapshot);

                    Tag tag = tagSnapshot.getValue(Tag.class);

                    if (null != tag) {
                        tag.setKey(tagSnapshot.getKey());
                        tag.setName(tagSnapshot.getKey());
                        list.add(tag);
                    } else {
                        Log.e(TAG, "Tag from DataSnapshot is null.");
                    }
                }
                callbacks.onTagsListSuccess(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callbacks.onTagsListFail(databaseError.getMessage());
                databaseError.toException().printStackTrace();
            }
        });
    }


    @Override
    public void updateCardTags(
            String cardKey,
            @Nullable HashMap<String,Boolean> oldTags,
            @Nullable HashMap<String,Boolean> newTags,
            @Nullable final iTagsSingleton.UpdateCallbacks callbacks
    ) {
        Log.d(TAG, "updateCardTags(cardKey: "+cardKey+", oldTags: "+oldTags+", newTags: "+newTags+")");

        if (null == oldTags) oldTags = new HashMap<>();
        if (null == newTags) newTags = new HashMap<>();

        Map<String, Boolean> addedTags = MyUtils.mapDiff(newTags, oldTags);
        Map<String, Boolean> removedTags = MyUtils.mapDiff(oldTags, newTags);

        HashMap<String,Object> updatePool = new HashMap<>();

        for (String tagName : addedTags.keySet()) {
            updatePool.put(tagName + "/cards/" + cardKey, true);
        }

        for (String tagName : removedTags.keySet()) {
            updatePool.put(tagName + "/cards/" + cardKey, null);
        }

        Log.d(TAG, "updatePool: "+updatePool);

        tagsRef.updateChildren(updatePool)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (null != callbacks) {
                            callbacks.onUpdateSuccess();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // TODO: сообщать, куда следует
                        if (null != callbacks) {
                            callbacks.onUpdateFail(e.getMessage());
                        }
                        Log.e(TAG, e.getMessage());
                        e.printStackTrace();
                    }
                });
    }


    @Override
    public void getTagsList() {
        DatabaseReference tagsCountRef = dbRef.child("/tags_count");

        tagsCountRef.orderByChild("value")
        .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot oneSnapshot : dataSnapshot.getChildren()) {
                    Log.d(TAG, "oneSnapshot: "+oneSnapshot);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
