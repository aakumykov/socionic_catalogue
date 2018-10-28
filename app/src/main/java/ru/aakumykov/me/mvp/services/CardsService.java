package ru.aakumykov.me.mvp.services;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.tasks.OnCanceledListener;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.interfaces.iCardsService;
import ru.aakumykov.me.mvp.models.Card;

public class CardsService extends Service implements
        iCardsService
{
    // Внутренний класс
    public class LocalBinder extends Binder {
        public CardsService getService() {
            return CardsService.this;
        }
    }


    // Свойства
    private final static String TAG = "CardsService";
    private final IBinder binder;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference cardsRef;

    private StorageReference imagesRef;
    private UploadTask uploadTask;


    // Слежебные методы
    public CardsService() {
//        Log.d(TAG, "new CardsService()");

        binder = new LocalBinder();

        firebaseDatabase = FirebaseDatabase.getInstance();
        cardsRef = firebaseDatabase.getReference().child(Constants.CARDS_PATH);
        imagesRef = FirebaseStorage.getInstance().getReference().child(Constants.IMAGES_PATH);
    }

    @Override
    public IBinder onBind(Intent intent) {
//        Log.d(TAG, "onBind()");
        return binder;
    }

    @Override
    public void onCreate() {
//        Log.d(TAG, "onCreate()");
        super.onCreate();

        firebaseDatabase.goOnline(); // нужно ли?
    }

    @Override
    public void onDestroy() {
//        Log.d(TAG, "onDestroy()");
        super.onDestroy();

        cancelUpload();

        firebaseDatabase.goOffline();
    }


    // Пользовательские методы
    @Override
    public String createKey() {
        return cardsRef.push().getKey();
    }

    @Override
    public void loadCard(String key, final CardCallbacks callbacks) {
        Log.d(TAG, "loadCard("+key+")");

        DatabaseReference cardRef = cardsRef.child(key);

        cardRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Card card = dataSnapshot.getValue(Card.class);
                callbacks.onLoadSuccess(card);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callbacks.onLoadFailed(databaseError.getMessage());
                databaseError.toException().printStackTrace();
            }
        });
    }

    @Override
    public void updateCard(final Card card, final SaveCardCallbacks callbacks) {
        Log.d(TAG, "updateCard(), "+card);

        DatabaseReference cardRef = cardsRef.child(card.getKey());

        cardRef.setValue(card)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callbacks.onCardSaveSuccess(card);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callbacks.onCardSaveError(e.getMessage());
                        e.printStackTrace();
                    }
                })
                .addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        callbacks.onCardSaveCancel();
                    }
                });
    }

    @Override
    public void deleteCard(final Card card, final  DeleteCallbacks callbacks) {
        Log.d(TAG, "deleteCard(), "+card);

        DatabaseReference cardRef = cardsRef.child(card.getKey());

        cardRef.removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (null == databaseError) {
                    callbacks.onDeleteSuccess(card);
                } else {
                    callbacks.onDeleteError(databaseError.getMessage());
                    databaseError.toException().printStackTrace();
                }
            }
        });
    }


    @Override
    public void loadList(ListCallbacks callbacks) {
        Log.d(TAG, "loadList()");
        loadList(null, callbacks);
    }

    @Override
    public void loadList(@Nullable String tagFilter, final ListCallbacks callbacks) {
        Log.d(TAG, "loadList(tagFilter: "+ tagFilter +", ...)");

        Query query = (null != tagFilter)
            ? cardsRef.orderByChild("tags/"+tagFilter).equalTo(true)
            : cardsRef.orderByKey();

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Card> list = new ArrayList<>();

                for (DataSnapshot snapshotPiece : dataSnapshot.getChildren()) {
                    try {
                        Card card = snapshotPiece.getValue(Card.class);

                        if (null != card) {
                            card.setKey(snapshotPiece.getKey());
                            list.add(card);
                        } else {
                           callbacks.onListLoadFail("Card from snapshotPiece is null");
                           Log.d(TAG, "snapshotPiece: "+snapshotPiece);
                        }

                    } catch (Exception e) {
                        // Здесь бы сообщение пользователю, но оно затрётся инфой
                        Log.e(TAG, e.getMessage()+", snapshotPiece: "+snapshotPiece);
                        e.printStackTrace();
                    }
                }

                callbacks.onListLoadSuccess(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callbacks.onListLoadFail(databaseError.getMessage());
                databaseError.toException().printStackTrace();
            }
        });
    }


    @Override
    public void uploadImage(final Uri imageURI, final String mimeType, final String remotePath, final ImageUploadCallbacks callbacks) {
        Log.d(TAG, "uploadImage()");

        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType(mimeType)
                .build();

        final StorageReference imageRef = imagesRef.child(remotePath);

        uploadTask = imageRef.putFile(imageURI, metadata);

        uploadTask
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        uploadTask = null;
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        long totalBytes = taskSnapshot.getTotalByteCount();
                        long uploadedBytes = taskSnapshot.getBytesTransferred();
                        int progress = Math.round((uploadedBytes/totalBytes)*100);
                        callbacks.onImageUploadProgress(progress);
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        imageRef.getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        callbacks.onImageUploadSuccess(uri);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        /* Что за хрень?
                                        Нужно ли здесь удалять файл? */
                                        callbacks.onImageUploadError(e.getMessage());
                                        e.printStackTrace();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callbacks.onImageUploadError(e.getMessage());
                    }
                })
                .addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        callbacks.onImageUploadCancel();
                    }
                });
    }

    @Override
    public void cancelUpload() {
        Log.d(TAG, "cancelUpload()");
        if (null != uploadTask && uploadTask.isInProgress()) uploadTask.cancel();
    }
}
