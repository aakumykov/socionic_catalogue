package ru.aakumykov.me.sociocat.utils;

import android.util.Log;

import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.singletons.CardsSingleton;
import ru.aakumykov.me.sociocat.singletons.StorageSingleton;
import ru.aakumykov.me.sociocat.singletons.iCardsSingleton;
import ru.aakumykov.me.sociocat.singletons.iStorageSingleton;

public class DeleteCard_Helper {

    public interface iDeletionCallbacks {
        void onCardDeleteSuccess(Card card);
        void onCardDeleteError(String errorMsg);
    }

    public static void deleteCard(String cardKey, iDeletionCallbacks callbacks) {
        CardsSingleton.getInstance().loadCard(cardKey, new iCardsSingleton.LoadCallbacks() {
            @Override
            public void onCardLoadSuccess(Card card) {
                deleteCardReal(card, new iCardDeletionCallbacksInternal() {
                    @Override
                    public void onCardDeleted(Card card) {
                        if (card.isImageCard())
                            deleteImageOfCard(card);

                        callbacks.onCardDeleteSuccess(card);
                    }

                    @Override
                    public void onCardNotDeleted(String errorMsg) {
                        callbacks.onCardDeleteError(errorMsg);
                    }
                });
            }

            @Override
            public void onCardLoadFailed(String msg) {
                callbacks.onCardDeleteError(msg);
            }
        });
    }


    private final static String TAG = DeleteCard_Helper.class.getSimpleName();

    private interface iCardDeletionCallbacksInternal {
        void onCardDeleted(Card card);
        void onCardNotDeleted(String errorMsg);
    }

    private static void deleteCardReal(Card card, iCardDeletionCallbacksInternal callbacks) {
        CardsSingleton.getInstance().deleteCard(card, new iCardsSingleton.DeleteCallbacks() {
            @Override
            public void onCardDeleteSuccess(Card card) {
                callbacks.onCardDeleted(card);
            }

            @Override
            public void onCardDeleteError(String msg) {
                callbacks.onCardNotDeleted(msg);
            }
        });
    }

    private static void deleteImageOfCard(Card card) {
        String imageFileName = card.getFileName();

        if (null == imageFileName) {
            Log.e(TAG, "Image file name of card "+card.getKey()+" cannot be NULL");
            return;
        }

        StorageSingleton.getInstance().deleteImage(imageFileName, new iStorageSingleton.FileDeletionCallbacks() {
            @Override
            public void onDeleteSuccess() {

            }

            @Override
            public void onDeleteFail(String errorMsg) {
                Log.e(TAG, "Error deleting image "+imageFileName+" of card "+card.getKey());
            }
        });
    }
}