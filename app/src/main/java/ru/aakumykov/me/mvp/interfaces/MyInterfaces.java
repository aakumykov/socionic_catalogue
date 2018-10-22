package ru.aakumykov.me.mvp.interfaces;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.telecom.Call;

import ru.aakumykov.me.mvp.card_edit.iCardEdit;
import ru.aakumykov.me.mvp.models.Card;

public interface MyInterfaces {

    // TODO: разобраться с бардаком в интерфейсах

    interface CardsService {

        void loadList(ListCallbacks callbacks);
        void loadCard(String key, CardCallbacks callbacks);

        String createKey();
        void saveCard(Card card, SaveCardCallbacks callbacks);
        void updateCard(Card newCard, UpdateCardCallbacks callbacks);
        void deleteCard(Card card, DeleteCallbacks callbacks);

        void uploadImage(Uri imageURI, String mimeType, String remotePath, ImageUploadCallbacks callbacks);
        void cancelUpload();


        interface CardCallbacks extends DeleteCallbacks, UpdateCardCallbacks {
            void onLoadSuccess(Card card);
            void onLoadFailed(String msg);
            void onLoadCanceled();
        }

        interface ListCallbacks extends DeleteCallbacks, UpdateCardCallbacks {
            void onChildAdded(Card card);
            void onChildChanged(Card card, String previousCardName); // или title?
            void onChildMoved(Card card, String previousCardName); // или title?
            void onCancelled(String errorMessage);
            void onBadData(String errorMsg);
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

        interface UpdateCardCallbacks {
            void onUpdateSuccess(Card card);
            void onUpdateError(String msg);
        }

        interface ImageUploadCallbacks {
            void onImageUploadProgress(int progress);
            void onImageUploadSuccess(Uri remoteImageURI);
            void onImageUploadError(String message);
            void onImageUploadCancel();
        }
    }

    interface DialogCallbacks {

        interface onCheck {
            boolean doCheck();
        }

        interface onYes {
            void yesAction();
        }

        interface onNo {
            void noAction();
        }
    }
}
