package ru.aakumykov.me.sociocat.singletons;

import android.graphics.Bitmap;

import androidx.annotation.Nullable;

public interface iStorageSingleton {



    @Deprecated
    void uploadAvatar(Bitmap bitmap, String imageType, @Nullable String fileNameWithoutExtension, FileUploadCallbacks callbacks);

    void uploadImage(Bitmap bitmap, String imageType, @Nullable String fileNameWithoutExtension, final FileUploadCallbacks callbacks);

    void uploadAvatar(byte[] imageBytes, String fileName, FileUploadCallbacks callbacks);

    void uploadCardImage(byte[] imageBytes, String fileName, FileUploadCallbacks callbacks);

    void deleteImage(@Nullable String imageFileName, FileDeletionCallbacks callbacks);
    void deleteAvatar(@Nullable String avatarFileName, FileDeletionCallbacks callbacks);


    interface ImageUploadCallbacks {
        void onImageUploaded();
    }

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
