package ru.aakumykov.me.mvp.interfaces;

import android.net.Uri;

public interface iStorageSingleton {

    void uploadImage(Uri localImageURI, String remoteImagePath, FileUploadCallbacks callbacks);
    void deleteImage(String remoteImagePath, FileDeleteCallbacks callbacks);

    interface FileUploadCallbacks {
        void onUploadProgress(int progress);
        void onUploadSuccess(String downloadURL);
        void onUploadFail(String errorMsg);
        void onUploadCancel();
    }

    interface FileDeleteCallbacks {
        void onDeleteSuccess();
        void onDeleteFail(String errorMSg);
    }
}
