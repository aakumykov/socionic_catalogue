package ru.aakumykov.me.sociocat.utils;

import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.singletons.CardsSingleton_CF;
import ru.aakumykov.me.sociocat.singletons.StorageSingleton;
import ru.aakumykov.me.sociocat.singletons.iCardsSingleton;
import ru.aakumykov.me.sociocat.singletons.iStorageSingleton;

public class CardDeletionHelper {

    public interface iDeletionCallbacks {
        void onCardDeleteSuccess(Card card);
        void onCardDeleteError(String errorMsg);
    }

    public static void deleteCard(Card card, iDeletionCallbacks callbacks) {

        String imageFileName = card.getFileName();
        if (null == imageFileName) {
            callbacks.onCardDeleteError("Image file name is NULL");
            return;
        }

        StorageSingleton.getInstance().deleteImage(imageFileName, new iStorageSingleton.FileDeletionCallbacks() {
            @Override
            public void onDeleteSuccess() {

                CardsSingleton_CF.getInstance().deleteCard(card, new iCardsSingleton.DeleteCallbacks() {
                    @Override
                    public void onCardDeleteSuccess(Card card) {
                        callbacks.onCardDeleteSuccess(card);
                    }

                    @Override
                    public void onCardDeleteError(String msg) {
                        callbacks.onCardDeleteError(msg);
                    }
                });

            }

            @Override
            public void onDeleteFail(String errorMsg) {
                callbacks.onCardDeleteError(errorMsg);
            }
        });
    }
}