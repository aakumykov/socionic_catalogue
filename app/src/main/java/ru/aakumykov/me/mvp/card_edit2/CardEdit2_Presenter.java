package ru.aakumykov.me.mvp.card_edit2;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;

import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.interfaces.iCardsSingleton;
import ru.aakumykov.me.mvp.interfaces.iTagsSingleton;
import ru.aakumykov.me.mvp.models.Card;
import ru.aakumykov.me.mvp.services.CardsSingleton;
import ru.aakumykov.me.mvp.services.TagsSingleton;
import ru.aakumykov.me.mvp.utils.MVPUtils;


public class CardEdit2_Presenter implements iCardEdit2.Presenter {

    private final static String TAG = "CardEdit2_Presenter";
    private iCardEdit2.View editView;
    private iCardsSingleton cardsService = CardsSingleton.getInstance();
    private iTagsSingleton tagsService = TagsSingleton.getInstance();

    private Card currentCard;
    private HashMap<String,Boolean> oldTags = null;


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
    public void setCardType(String cardType) {
        if (MVPUtils.isCorrectCardType(cardType))
            currentCard.setType(cardType);
    }

    @Override
    public void processTag(String tagName) {
        tagName = MVPUtils.normalizeTag(tagName );
        if (!TextUtils.isEmpty(tagName))
            editView.addTag(tagName);
    }

    @Override
    public void saveCard() {

        currentCard.setTitle(editView.getCardTitle());
        currentCard.setQuote(editView.getCardQuote());
        currentCard.setDescription(editView.getCardDescription());
        if (currentCard.getType().equals(Constants.VIDEO_CARD))
            currentCard.setVideoCode(editView.getVideoCode());

        editView.showProgressBar();
        editView.disableForm();

        cardsService.updateCard(currentCard, new iCardsSingleton.SaveCardCallbacks() {

            @Override
            public void onCardSaveSuccess(Card card) {
                editView.showToast(R.string.CARD_EDIT_card_saved);

                final Card savedCard = card;

                HashMap<String,Boolean> newTags = editView.getCardTags();
                tagsService.updateCardTags(currentCard.getKey(), oldTags, newTags, new iTagsSingleton.UpdateCallbacks() {
                    @Override
                    public void onUpdateSuccess() {
                        editView.showToast(R.string.CARD_EDIT_tags_are_saved);
                        editView.finishEdit(savedCard);
                    }

                    @Override
                    public void onUpdateFail(String errorMsg) {
                        Log.e(TAG, errorMsg);
                        editView.showErrorMsg(R.string.CARD_EDIT_error_saving_tags, errorMsg);
                        editView.finishEdit(savedCard);
                    }
                });
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
        currentCard = new Card();
        currentCard.setKey(cardsService.createKey());

        editView.hideProgressBar();
        editView.showModeSwitcher();
    }

    private void continueWithRecievedData(@NonNull Intent intent) {
        editView.hideProgressBar();
    }

    private void loadCard(@NonNull Intent intent) {

        editView.disableForm();

        String cardId = intent.getStringExtra(Constants.CARD_KEY);
        if (null == cardId) {
            editView.showErrorMsg(R.string.CARD_EDIT_error_no_input_data);
            return;
        }

        try {
            cardsService.loadCard(cardId, new iCardsSingleton.LoadCallbacks() {

                @Override
                public void onCardLoadSuccess(Card card) {
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

        currentCard = card;
        oldTags = card.getTags();

        editView.enableForm();

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
