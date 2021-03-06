package ru.aakumykov.me.sociocat.singletons;

import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import ru.aakumykov.me.sociocat.constants.Constants;
import ru.aakumykov.me.sociocat.utils.MVPUtils.MVPUtils;
import ru.aakumykov.me.sociocat.utils.MyUtils;

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
    private StorageReference rootRef = firebaseStorage.getReference().child("/");
    private StorageReference imagesRef = firebaseStorage.getReference().child(Constants.IMAGES_PATH);
//    private StorageReference avatarsRef = firebaseStorage.getReference().child(Constants.AVATARS_PATH);
    private String fileName;


    // Интерфейсные методы
    @Override @Deprecated
    public void uploadImage(Bitmap imageBitmap, String imageType, @Nullable String fileNameWithoutExtension, final FileUploadCallbacks callbacks) {

        if (TextUtils.isEmpty(fileNameWithoutExtension)) {
            callbacks.onFileUploadFail("Image file name cannot be empty: "+ fileNameWithoutExtension);

            return;
        }

        byte[] imageBytesArray = MVPUtils.compressImage(imageBitmap, imageType);

        String fileName = fileNameWithoutExtension + "." + imageType;

        uploadFile(imageBytesArray, Constants.IMAGES_PATH, fileName, callbacks);
    }

    @Override @Deprecated
    public void uploadAvatar(Bitmap imageBitmap, String imageType, @Nullable String fileNameWithoutExtension, final FileUploadCallbacks callbacks) {

        if (TextUtils.isEmpty(fileNameWithoutExtension)) {
            callbacks.onFileUploadFail("Image file name cannot be empty: "+fileNameWithoutExtension);
            return;
        }

        byte[] imageBytesArray = MVPUtils.compressImage(imageBitmap, imageType);

        String fileName = fileNameWithoutExtension + "." + imageType;

        uploadFile(imageBytesArray, Constants.AVATARS_PATH, fileName, callbacks);
    }

    @Override
    public void uploadAvatar(byte[] imageBytes, String fileName, final FileUploadCallbacks callbacks) {
        uploadFile(imageBytes, Constants.AVATARS_PATH, fileName, callbacks);
    }

    @Override
    public void uploadCardImage(byte[] imageBytes, String fileName, final FileUploadCallbacks callbacks) {
        uploadFile(imageBytes, Constants.IMAGES_PATH, fileName, callbacks);
    }

    @Override
    public void deleteImage(@Nullable String imageFileName, final FileDeletionCallbacks callbacks) {

        if (TextUtils.isEmpty(imageFileName)) {
            callbacks.onDeleteFail("Image file name cannot be empty: "+imageFileName);
            return;
        }

        String filePath = "/" + Constants.IMAGES_PATH +"/"+ imageFileName;
        deleteFile(filePath, callbacks);
    }

    @Override
    public void deleteAvatar(@Nullable String avatarFileName, FileDeletionCallbacks callbacks) {

        if (TextUtils.isEmpty(avatarFileName)) {
            callbacks.onDeleteFail("Image file name cannot be empty: "+avatarFileName);
            return;
        }

        String filePath = "/" + Constants.AVATARS_PATH +"/"+ avatarFileName;
        deleteFile(filePath, callbacks);
    }

    @Override
    public void getImage(@Nullable String imageFileName, GetFileCallbacks callbacks) {
        if (TextUtils.isEmpty(imageFileName)) {
            callbacks.onFileDownloadError("Image file name cannot be empty");
            return;
        }

        Runtime rt = Runtime.getRuntime();
        long maxMemory = rt.maxMemory();

        imagesRef.child(imageFileName).getBytes(maxMemory)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        callbacks.onFileDownloadSuccess(bytes);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callbacks.onFileDownloadError(e.getMessage());
                        MyUtils.printError(TAG, e);
                    }
                });
    }


    // Внутренние методы
    private void uploadFile(Uri fileURI, String remoteDirectoryName, @Nullable String remoteFileName, final iStorageSingleton.FileUploadCallbacks callbacks) {

        final StorageReference fileRef = rootRef.child(remoteDirectoryName+"/"+remoteFileName);
        UploadTask uploadTask = fileRef.putFile(fileURI);
        doUpload(uploadTask, fileRef, callbacks);
    }
    
    private void uploadFile(byte[] fileBytesArray, String remoteDirectoryName, String remoteFileName, iStorageSingleton.FileUploadCallbacks callbacks) {

        this.fileName = remoteFileName;

        final StorageReference fileRef = rootRef.child(remoteDirectoryName+"/"+remoteFileName);
        UploadTask uploadTask = fileRef.putBytes(fileBytesArray);
        doUpload(uploadTask, fileRef, callbacks);
    }
    
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
                                        callbacks.onFileUploadSuccess(fileName, uri.toString());
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

    private void deleteFile(String filePath, final iStorageSingleton.FileDeletionCallbacks callbacks) {

        StorageReference fileRef = rootRef.child(filePath);

        fileRef.delete()
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
}
