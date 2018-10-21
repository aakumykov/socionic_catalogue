package ru.aakumykov.me.mvp.interfaces;

import android.support.annotation.Nullable;
import android.telecom.Call;

import ru.aakumykov.me.mvp.models.Card;

public interface MyInterfaces {

    interface CardsService {

        void loadCard(String key, CardCallbacks callbacks);
        void loadList(ListCallbacks callbacks);
        void updateCard(String key, Card newCard);
        void deleteCard(Card card, DeleteCallbacks callbacks);

        interface CardCallbacks extends DeleteCallbacks  {
            void onLoadSuccess(Card card);
            void onLoadFailed(String msg);
            void onLoadCanceled();
        }

        interface ListCallbacks extends DeleteCallbacks {
            void onChildAdded(Card card);
            void onChildChanged(Card card, String previousCardName); // или title?
            void onChildMoved(Card card, String previousCardName); // или title?
            void onCancelled(String errorMessage);
            void onBadData(String errorMsg);
        }

        // Используется в CardCallbacks и ListCallbacks
        interface DeleteCallbacks {
            void onDeleteSuccess(Card card);
            void onDeleteError(String msg);
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
