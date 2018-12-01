package ru.aakumykov.me.mvp.services;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.interfaces.iStorageSingleton;

public class StorageSingleton implements iStorageSingleton {

    /* Одиночка */
    private static volatile StorageSingleton ourInstance;
    public synchronized static StorageSingleton getInstance() {
        synchronized (StorageSingleton.class) {
            if (null == ourInstance) ourInstance = new StorageSingleton();
            return ourInstance;
        }
    }
    private StorageSingleton() {}
    /* Одиночка */

    private final static String TAG = "StorageSingleton";
    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    private StorageReference imagesRef = firebaseStorage.getReference().child(Constants.IMAGES_PATH);


    // Интерфейсные методы
    @Override
    public void uploadImage(Uri localImageURI, String remoteImagePath, final iStorageSingleton.FileUploadCallbacks callbacks) {

        final StorageReference theImageRef = imagesRef.child("/"+remoteImagePath);
        UploadTask uploadTask = theImageRef.putFile(localImageURI);
        doUpload(uploadTask, theImageRef, callbacks);
    }

    @Override
    public void uploadImage(byte[] imageByteArray, String remoteImagePath, final iStorageSingleton.FileUploadCallbacks callbacks) {

        final StorageReference theImageRef = imagesRef.child("/"+remoteImagePath);
        UploadTask uploadTask = theImageRef.putBytes(imageByteArray);
        doUpload(uploadTask, theImageRef, callbacks);
    }

    @Override
    public void deleteImage(String remoteImagePath, final iStorageSingleton.FileDeleteCallbacks callbacks) {

        final StorageReference theImageRef = imagesRef.child("/"+remoteImagePath);

        theImageRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callbacks.onDeleteSuccess();
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


    // Внутренние методы
    private void doUpload(final UploadTask uploadTask, final StorageReference storageReference, final iStorageSingleton.FileUploadCallbacks callbacks) {
        uploadTask
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        Long totalBytes = taskSnapshot.getTotalByteCount();
                        Long uploadedBytes = taskSnapshot.getBytesTransferred();
                        int progress = Math.round((uploadedBytes / totalBytes) * 100);
                        callbacks.onFileUploadProgress(progress);
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        // Получаю ссылку для скачивания
                        storageReference.getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        callbacks.onFileUploadSuccess(uri.toString());
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        callbacks.onFileUploadFail(e.getMessage());
                                        e.printStackTrace();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callbacks.onFileUploadFail(e.getMessage());
                        e.printStackTrace();
                    }
                })
                .addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        callbacks.onFileUploadCancel();
                    }
                });
    }
//    private void uploadFile(Uri localFileURI, String remoteFilePath, iStorageSingleton.FileUploadCallbacks callbacks) {
//
//    }

    private void deleteFile(String remoteFilePath, iStorageSingleton.FileDeleteCallbacks callbacks) {

    }
}
