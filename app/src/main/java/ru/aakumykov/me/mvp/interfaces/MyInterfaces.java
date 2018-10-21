package ru.aakumykov.me.mvp.interfaces;

import android.support.annotation.Nullable;
import android.telecom.Call;

import ru.aakumykov.me.mvp.models.Card;

public interface MyInterfaces {

    interface CardsService {
        void loadCard(String key, CardCallbacks callbacks);
        void loadList(ListCallbacks callbacks);

        interface CardCallbacks {
            void onLoadSuccess(Card card);
            void onLoadFailed(String msg);
            void onLoadCanceled();
            void onDeleteComplete(@Nullable String msg);
        }

        interface ListCallbacks {
            void onChildAdded(Card card);
            void onChildChanged(Card card, String previousCardName); // или title?
            void onChildRemoved(Card card);
            void onChildMoved(Card card, String previousCardName); // или title?
            void onCancelled(String errorMessage);
            void onBadData(String errorMsg);
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
