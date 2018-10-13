package ru.aakumykov.me.mvp.card_edit;

import android.arch.lifecycle.MutableLiveData;

import ru.aakumykov.me.mvp.models.Card;

public interface iCardEdit {

    interface View {
        void showError(String msg);
        void hideError();

        void fillEditForm(Card card);

        void enableSaveButton();
        void disableSaveButton();
    }

    interface ViewModel {
        MutableLiveData<Card> getCardLiveData();
        MutableLiveData<String> getErrorLiveData();
    }

    interface Model {
        void saveCard(Card card, iCardEdit.ModelCallbacks callbacks);
    }

    interface ModelCallbacks {
        void onLoadSuccess(Card card);
        void onLoadError(String message);
    }
}
