package ru.aakumykov.me.mvp.card_edit2;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.HashMap;

import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.interfaces.iCardsSingleton;
import ru.aakumykov.me.mvp.interfaces.iTagsSingleton;
import ru.aakumykov.me.mvp.models.Card;
import ru.aakumykov.me.mvp.services.CardsSingleton;
import ru.aakumykov.me.mvp.services.TagsSingleton;
import ru.aakumykov.me.mvp.utils.MVPUtils.MVPUtils;
import ru.aakumykov.me.mvp.utils.MyUtils;


public class CardEdit2_Presenter implements
        iCardEdit2.Presenter,
        iCardsSingleton.SaveCardCallbacks
{

    private final static String TAG = "CardEdit2_Presenter";
    private iCardEdit2.View editView;
    private iCardsSingleton cardsService = CardsSingleton.getInstance();
    private iTagsSingleton tagsService = TagsSingleton.getInstance();

    private Card currentCard;
    private HashMap<String,Boolean> oldTags = null;
    private HashMap<String,Boolean> newTags = null;


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

        newTags = editView.getCardTags();
        // Как-то это не очень: здесь устанавливаю, потом  в другом месте обновляю...
        currentCard.setTags(newTags);

        editView.showProgressBar();
        editView.disableForm();

        cardsService.updateCard(currentCard, this);
    }


    // Методы оратного вызова
    @Override
    public void onCardSaveSuccess(Card card) {

        tagsService.updateCardTags(
                currentCard.getKey(),
                oldTags,
                newTags,
                null
        );

        editView.hideProgressBar();
        editView.finishEdit(card);
    }

    @Override
    public void onCardSaveError(String message) {
        editView.showErrorMsg(R.string.CARD_EDIT_error_saving_card);
        editView.enableForm();
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

        currentCard = new Card();
        currentCard.setKey(cardsService.createKey());

        switch (MVPUtils.detectInputDataMode(intent)) {

            case Constants.TYPE_TEXT:
                try {
                    procesIncomingText(intent);
                } catch (Exception e) {
                    editView.showErrorMsg(R.string.CARD_EDIT_error_processing_data, e.getMessage());
                    e.printStackTrace();
                }
                break;

            case Constants.TYPE_IMAGE_DATA:
//                processIncomingImage(intent);
                break;

            case Constants.TYPE_YOUTUBE_VIDEO:
//                String link = intent.getStringExtra(Intent.EXTRA_TEXT);
//                processYoutubeVideo(link);
                break;

            default:
                editView.showErrorMsg(R.string.CARD_EDIT_unknown_data_mode);
        }
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

    // Для полученных данных
    private void procesIncomingText(Intent intent) throws Exception {

        editView.switchTextMode(currentCard);

        String text = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (null == text) {
            throw new IllegalArgumentException("Intent.EXTRA_TEXT is null.");
        }

        String autoTitle = MyUtils.cutToLength(text, Constants.TITLE_MAX_LENGTH);

        editView.setCardTitle(autoTitle);
        editView.setCardQuote(text);
    }

}
