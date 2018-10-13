package ru.aakumykov.me.mvp.card_edit;

import android.arch.lifecycle.MutableLiveData;

import ru.aakumykov.me.mvp.models.Card;

public interface iCardEdit {

    interface View {
        void fillEditForm(Card card);
        void saveCard();
        void cancelEdit();

        void showMessage(int msgId, String msgType);
        void hideMessage();

        void enableEditForm();
        void disableEditForm();

        void showProgressBar();
        void hideProgressBar();
    }

    interface ViewModel {
        MutableLiveData<Card> getCardLiveData();
        MutableLiveData<String> getErrorLiveData();
    }

    interface Model {
        void saveCard(Card card, iCardEdit.ModelCallbacks callbacks);
    }

    interface ModelCallbacks {
        void onSaveSuccess();
        void onSaveError(String message);
        void onSaveCancel();
    }
}
