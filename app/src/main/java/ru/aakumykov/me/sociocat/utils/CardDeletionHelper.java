package ru.aakumykov.me.sociocat.utils;

import android.util.Log;

import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.singletons.CardsSingleton;
import ru.aakumykov.me.sociocat.singletons.CommentsSingleton_CF;
import ru.aakumykov.me.sociocat.singletons.StorageSingleton;
import ru.aakumykov.me.sociocat.singletons.iCardsSingleton;
import ru.aakumykov.me.sociocat.singletons.iStorageSingleton;

public class CardDeletionHelper {

    private final static String TAG = "CardDeletionHelper";


    public interface iDeletionCallbacks {
        void onCardDeleteSuccess(Card card);
        void onCardDeleteError(String errorMsg);
    }

    public static void deleteCard(Card card, iDeletionCallbacks callbacks) {

        if (card.isImageCard()) {

            String imageFileName = card.getFileName();
            if (null == imageFileName) {
                callbacks.onCardDeleteError("Image file name is NULL");
                return;
            }

            StorageSingleton.getInstance().deleteImage(imageFileName, new iStorageSingleton.FileDeletionCallbacks() {
                @Override
                public void onDeleteSuccess() {
                    deleteCardReal(card, callbacks);
                }

                @Override
                public void onDeleteFail(String errorMsg) {
                    callbacks.onCardDeleteError(errorMsg);
                }
            });
        }
        else {
            deleteCardReal(card, callbacks);
        }
    }

    private static void deleteCardReal(Card card, iDeletionCallbacks callbacks) {

        CardsSingleton.getInstance().deleteCard(card, new iCardsSingleton.DeleteCallbacks() {
            @Override
            public void onCardDeleteSuccess(Card card) {
                try {
                    CommentsSingleton_CF.getInstance().deleteCommentsForCard(card.getKey());
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                    e.printStackTrace();
                }

                callbacks.onCardDeleteSuccess(card);
            }

            @Override
            public void onCardDeleteError(String msg) {
                callbacks.onCardDeleteError(msg);
            }
        });
    }
}