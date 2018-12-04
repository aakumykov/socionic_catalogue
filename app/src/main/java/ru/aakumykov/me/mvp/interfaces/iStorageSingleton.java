package ru.aakumykov.me.mvp.interfaces;

import android.net.Uri;

public interface iStorageSingleton {

    void uploadImage(Uri localImageURI, String remoteImagePath, FileUploadCallbacks callbacks);
    void uploadImage(byte[] imageByteArray, String remoteImagePath, FileUploadCallbacks callbacks);
    void uploadAvatar(byte[] imageByteArray, String remoteImagePath, FileUploadCallbacks callbacks);
    void deleteImage(String remoteImagePath, FileDeleteCallbacks callbacks);

    interface FileUploadCallbacks {
        void onFileUploadProgress(int progress);
        void onFileUploadSuccess(String downloadURL);
        void onFileUploadFail(String errorMsg);
        void onFileUploadCancel();
    }

    interface FileDeleteCallbacks {
        void onDeleteSuccess();
        void onDeleteFail(String errorMSg);
    }
}
