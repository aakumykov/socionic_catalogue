package ru.aakumykov.me.sociocat.utils;

import android.util.Log;

import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.singletons.CardsSingleton;
import ru.aakumykov.me.sociocat.singletons.CommentsSingleton;
import ru.aakumykov.me.sociocat.singletons.StorageSingleton;
import ru.aakumykov.me.sociocat.singletons.iCardsSingleton;
import ru.aakumykov.me.sociocat.singletons.iStorageSingleton;

public class CardDeletionHelper {

    private final static String TAG = "CardDeletionHelper";


    public interface iDeletionCallbacks {
        void onCardDeleteSuccess(Card card);
        void onCardDeleteError(String errorMsg);
    }

    public static void deleteCard(String cardKey, iDeletionCallbacks callbacks) {

        // Получаю свежий объект карточки с сервера
        CardsSingleton.getInstance().loadCard(cardKey, new iCardsSingleton.LoadCallbacks() {
            @Override
            public void onCardLoadSuccess(Card card) {

                // Если это карточка с картинкой
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
                // Если без картинки
                else {
                    deleteCardReal(card, callbacks);
                }
            }

            @Override
            public void onCardLoadFailed(String msg) {
                callbacks.onCardDeleteError(msg);
            }
        });
    }

    private static void deleteCardReal(Card card, iDeletionCallbacks callbacks) {

        CardsSingleton.getInstance().deleteCard(card, new iCardsSingleton.DeleteCallbacks() {
            @Override
            public void onCardDeleteSuccess(Card card) {
                try {
                    CommentsSingleton.getInstance().deleteCommentsForCard(card.getKey());
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