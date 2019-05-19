package ru.aakumykov.me.sociocat.singletons;

import android.graphics.Bitmap;

public interface iStorageSingleton {

    void uploadImage(Bitmap bitmap, String imageType, String fileName, final iStorageSingleton.FileUploadCallbacks callbacks);
//    void uploadImage(Uri localImageURI, String remoteImagePath, FileUploadCallbacks callbacks);
//    void uploadImage(byte[] imageByteArray, String remoteImagePath, FileUploadCallbacks callbacks);
    void uploadAvatar(Bitmap bitmap, String imageType, String remoteImagePath, FileUploadCallbacks callbacks);
    void deleteImage(String imageFileName, FileDeletionCallbacks callbacks);
    void deleteAvatar(String avatarFileName, FileDeletionCallbacks callbacks);

    interface FileUploadCallbacks {
        void onFileUploadProgress(int progress);
        void onFileUploadSuccess(String fileName, String downloadURL);
        void onFileUploadFail(String errorMsg);
        void onFileUploadCancel();
    }

    interface FileDeletionCallbacks {
        void onDeleteSuccess();
        void onDeleteFail(String errorMSg);
    }
}
