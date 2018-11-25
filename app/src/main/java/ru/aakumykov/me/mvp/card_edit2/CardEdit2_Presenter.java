package ru.aakumykov.me.mvp.card_edit2;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.models.Card;
import ru.aakumykov.me.mvp.utils.MVPUtils;


public class CardEdit2_Presenter implements iCardEdit2.Presenter {

    private iCardEdit2.View editView;


    // Обязательные методы
    @Override
    public void linkView(iCardEdit2.View view) {
        editView = view;
    }

    @Override
    public void unlinkView() {
        editView = null;
    }


    // Интерфейсные методы
    @Override
    public void processInputIntent(@Nullable Intent intent) throws Exception {

        if (null == intent)
            throw new IllegalArgumentException("Input intent is null.");

        String action = intent.getAction();
        switch (action) {
            case Constants.ACTION_CREATE:
                proceedWithCardCreation(intent);
                break;
            case Intent.ACTION_SEND:
                processRecievedData(intent);
                break;
            case Constants.ACTION_EDIT:
                proceedWithCardEdition(intent);
                break;
            default:
                throw new IllegalArgumentException("Unknown action '"+action+"'");
        }
    }

    @Override
    public void saveCard() {

    }


    // Внутренние методы
    private void proceedWithCardCreation(@NonNull Intent intent) {

    }

    private void processRecievedData(@NonNull Intent intent) {

    }

    private void proceedWithCardEdition(@NonNull Intent intent) {
        Card card = intent.getParcelableExtra(Constants.CARD);
        if (null == card)
            throw new IllegalArgumentException("Card from Intent is null.");

        switch (card.getType()) {
            case Constants.TEXT_CARD:
                editView.switchTextMode(card);
                break;
            case Constants.IMAGE_CARD:
                editView.switchImageMode(card);
                break;
            case Constants.VIDEO_CARD:
                editView.switchVideoMode(card);
                break;
            default:
                throw new IllegalArgumentException("Unsupported card type '"+card.getType()+"'");
        }
    }
}
