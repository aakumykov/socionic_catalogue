package ru.aakumykov.me.sociocat.singletons;

import android.graphics.Bitmap;

import androidx.annotation.Nullable;

public interface iStorageSingleton {

    void uploadImage(Bitmap bitmap, String imageType, @Nullable String fileNameWithoutExtension, final FileUploadCallbacks callbacks);
    void uploadAvatar(Bitmap bitmap, String imageType, @Nullable String fileNameWithoutExtension, FileUploadCallbacks callbacks);
    void deleteImage(@Nullable String imageFileName, FileDeletionCallbacks callbacks);
    void deleteAvatar(@Nullable String avatarFileName, FileDeletionCallbacks callbacks);

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
