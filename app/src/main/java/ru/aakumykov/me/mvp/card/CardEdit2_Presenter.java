package ru.aakumykov.me.mvp.card;

import android.content.Intent;
import android.util.Log;

import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.interfaces.iAuthSingleton;
import ru.aakumykov.me.mvp.interfaces.iCardsSingleton;
import ru.aakumykov.me.mvp.interfaces.iStorageSingleton;
import ru.aakumykov.me.mvp.services.AuthSingleton;
import ru.aakumykov.me.mvp.services.CardsSingleton;
import ru.aakumykov.me.mvp.services.StorageSingleton;

public class CardEdit2_Presenter implements iCardEdit2.Presenter {

    private final static String TAG = "CardEdit2_Presenter";
    private iCardEdit2.View view;
    private iCardsSingleton cardsService = CardsSingleton.getInstance();
    private iAuthSingleton authService = AuthSingleton.getInstance();
    protected iStorageSingleton storageService = StorageSingleton.getInstance();

    @Override
    public void linkView(iCardEdit2.View view) {
        this.view = view;
    }

    @Override
    public void unlinkView() {
        this.view = null;
    }

    @Override
    public void processInputIntent(Intent intent) throws Exception {
//        view.showProgressBar();

        if (null == intent) {
            throw new IllegalArgumentException("Intent is null");
        }

        String action = intent.getAction();
        switch (action+"") {

            case Constants.ACTION_CREATE:
                createCard(intent);
                break;

            case Constants.ACTION_EDIT:
                editCard(intent);
                break;

            case Intent.ACTION_SEND:
                recieveData(intent);
                break;

            default:
                throw new IllegalArgumentException("Unknown action '"+action+"'");
        }


    }

    @Override
    public void saveCard() throws Exception {

    }


    // Внутренние методы
    private void createCard(Intent intent) {
        Log.d(TAG, "createCard()");
    }

    private void editCard(Intent intent) {
        Log.d(TAG, "editCard()");
    }

    private void recieveData(Intent intent) {
        Log.d(TAG, "recieveData()");
    }
}
