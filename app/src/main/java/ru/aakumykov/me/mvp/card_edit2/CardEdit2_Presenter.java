package ru.aakumykov.me.mvp.card_edit2;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.interfaces.iCardsSingleton;
import ru.aakumykov.me.mvp.models.Card;
import ru.aakumykov.me.mvp.services.CardsSingleton;


public class CardEdit2_Presenter implements iCardEdit2.Presenter {

    private iCardsSingleton cardsService = CardsSingleton.getInstance();
    private iCardEdit2.View editView;
    private Card currentCard;

    // Интерфейсные методы
    @Override
    public void processInputIntent(@Nullable Intent intent) {

        editView.showProgressBar();

        if (null == intent) {
            editView.showErrorMsg(R.string.CARD_EDIT_error_no_input_data);
            return;
        }

        String action = intent.getAction();
        switch (action+"") {

            case Constants.ACTION_CREATE:
                continueWithCardCreation(intent);
                break;

            case Intent.ACTION_SEND:
                continueWithRecievedData(intent);
                break;

            case Constants.ACTION_EDIT:
                loadCard(intent);
                break;

            default:
                throw new IllegalArgumentException("Unknown action '"+action+"'");
        }
    }

    @Override
    public void saveCard() {

        currentCard.setTitle(editView.getCardTitle());
        currentCard.setQuote(editView.getCardQuote());
        currentCard.setDescription(editView.getCardDescription());
        if (currentCard.getType().equals(Constants.VIDEO_CARD))
            currentCard.setVideoCode(editView.getVideoCode());

//        currentCard.setTags(editView.getCardTags());

        editView.showProgressBar();
        editView.disableForm();

        cardsService.updateCard(currentCard, new iCardsSingleton.SaveCardCallbacks() {
            @Override
            public void onCardSaveSuccess(Card card) {
                editView.showToast(R.string.CARD_EDIT_card_saved);
                editView.finishEdit(card);
            }

            @Override
            public void onCardSaveError(String message) {
                editView.showErrorMsg(R.string.CARD_EDIT_error_saving_card, message);
                editView.enableForm();
            }
        });
    }


    // Обязательные методы
    @Override
    public void linkView(iCardEdit2.View view) {
        editView = view;
    }
    @Override
    public void unlinkView() {
        editView = null;
    }


    // Внутренние методы
    private void continueWithCardCreation(@NonNull Intent intent) {
        editView.hideProgressBar();
        editView.showModeSwitcher();
    }

    private void continueWithRecievedData(@NonNull Intent intent) {
        editView.hideProgressBar();
    }

    private void loadCard(@NonNull Intent intent) {


        String cardId = intent.getStringExtra(Constants.CARD_KEY);
        if (null == cardId) {
            editView.showErrorMsg(R.string.CARD_EDIT_error_no_input_data);
            return;
        }

        try {
            cardsService.loadCard(cardId, new iCardsSingleton.LoadCallbacks() {

                @Override
                public void onCardLoadSuccess(Card card) {
                    currentCard = card;
                    try {
                        editView.hideProgressBar();
                        processLoadedCard(card);
                    } catch (Exception e) {
                        editView.showErrorMsg(R.string.CARD_EDIT_error_editing_card, e.getMessage());
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCardLoadFailed(String msg) {
                    editView.showErrorMsg(R.string.CARD_EDIT_error_loading_card, msg);
                }
            });
        } catch (Exception e) {
            editView.showErrorMsg(R.string.CARD_EDIT_error_loading_card, e.getMessage());
            e.printStackTrace();
        }
    }

    private void processLoadedCard(@Nullable Card card) throws Exception {

        if (null == card)
            throw new IllegalArgumentException("Card is null.");

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
