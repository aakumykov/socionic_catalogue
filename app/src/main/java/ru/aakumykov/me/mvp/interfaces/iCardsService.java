package ru.aakumykov.me.mvp.interfaces;

import android.net.Uri;

import java.util.List;

import ru.aakumykov.me.mvp.models.Card;

public interface iCardsService {

    void loadList(ListCallbacks callbacks);
    void loadCard(String key, CardCallbacks callbacks);

    String createKey();
    void updateCard(Card card, SaveCardCallbacks callbacks);
    void deleteCard(Card card, DeleteCallbacks callbacks);

    void uploadImage(Uri imageURI, String mimeType, String remotePath, ImageUploadCallbacks callbacks);
    void cancelUpload();


    interface CardCallbacks extends DeleteCallbacks {
        void onLoadSuccess(Card card);
        void onLoadFailed(String msg);
        void onLoadCanceled();
    }

    interface ListCallbacks extends DeleteCallbacks {
        void onListLoadSuccess(List<Card> list);
        void onListLoadFail(String errorMessage);
    }

    interface SaveCardCallbacks {
        void onCardSaveSuccess(Card card);
        void onCardSaveError(String message);
        void onCardSaveCancel();
    }

    interface DeleteCallbacks {
        void onDeleteSuccess(Card card);
        void onDeleteError(String msg);
    }

    interface ImageUploadCallbacks {
        void onImageUploadProgress(int progress);
        void onImageUploadSuccess(Uri remoteImageURI);
        void onImageUploadError(String message);
        void onImageUploadCancel();
    }
}
