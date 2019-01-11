package ru.aakumykov.me.mvp.interfaces;

import android.graphics.Bitmap;
import android.net.Uri;

public interface iStorageSingleton {

    void uploadImage(Bitmap bitmap, String imageType, String fileName, final iStorageSingleton.FileUploadCallbacks callbacks);
//    void uploadImage(Uri localImageURI, String remoteImagePath, FileUploadCallbacks callbacks);
//    void uploadImage(byte[] imageByteArray, String remoteImagePath, FileUploadCallbacks callbacks);
    void uploadAvatar(Bitmap bitmap, String imageType, String remoteImagePath, FileUploadCallbacks callbacks);
    void deleteImage(String remoteImagePath, FileDeleteCallbacks callbacks);

    interface FileUploadCallbacks {
        void onFileUploadProgress(int progress);
        void onFileUploadSuccess(String fileName, String downloadURL);
        void onFileUploadFail(String errorMsg);
        void onFileUploadCancel();
    }

    interface FileDeleteCallbacks {
        void onDeleteSuccess();
        void onDeleteFail(String errorMSg);
    }
}
