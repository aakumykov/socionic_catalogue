package ru.aakumykov.me.mvp.card_edit;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.models.Card;

public class CardEdit_Model implements iCardEdit.Model {

    /* Одиночка */
    private static volatile CardEdit_Model ourInstance = new CardEdit_Model();
    private CardEdit_Model() { }
    public static synchronized CardEdit_Model getInstance() {
        synchronized (CardEdit_Model.class) {
            if (null == ourInstance) {
                ourInstance = new CardEdit_Model();
            }
            return ourInstance;
        }
    }
    /* Одиночка */

    private final static String TAG = "CardEdit_Model";
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

    @Override
    public String createKey() {
        return firebaseDatabase.getReference()
                .child(Constants.CARDS_PATH)
                .push().getKey();
    }

    @Override
    public void uploadImage(Uri imageURI, String mimeType, String remotePath, final iCardEdit.ModelCallbacks callbacks) {
        Log.d(TAG, "uploadImage()");

        final StorageReference imageRef = firebaseStorage.getReference().child(remotePath);

        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType(mimeType)
                .build();

        imageRef.putFile(imageURI, metadata)
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        long totalBytes = taskSnapshot.getTotalByteCount();
                        long uploadedBytes = taskSnapshot.getBytesTransferred();
                        int progress = (int) Math.round((uploadedBytes/totalBytes)*100);
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
    public void saveCard(final Card card, final iCardEdit.ModelCallbacks callbacks) {
        Log.d(TAG, "saveCard(), "+card);

        DatabaseReference cardRef = firebaseDatabase.getReference()
                .child(Constants.CARDS_PATH).child(card.getKey());

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
}
