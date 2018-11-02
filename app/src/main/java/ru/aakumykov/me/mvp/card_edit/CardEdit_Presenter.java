package ru.aakumykov.me.mvp.card_edit;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;

import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.utils.MyUtils;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.interfaces.iCardsService;
import ru.aakumykov.me.mvp.models.Card;
import ru.aakumykov.me.mvp.services.TagsSingleton;


public class CardEdit_Presenter extends android.arch.lifecycle.ViewModel implements
        iCardEdit.Presenter,
        iCardsService.ImageUploadCallbacks,
        iCardsService.SaveCardCallbacks
{

    private final static String TAG = "CardEdit_Presenter";
    private iCardEdit.View view;
    private iCardsService model;

    private Card currentCard;
    private Uri localImageURI;
    private String localImageType;
    private String newImageURI;
    private HashMap<String,Boolean> oldTags = new HashMap<>();
    private HashMap<String,Boolean> newTags;


    @Override
    public void processInputIntent(Intent intent) throws Exception {
        Log.d(TAG, "processInputIntent()");

        if (null == intent) {
            Log.e(TAG, "Intent == null");
            return;
        }

        String action = intent.getAction();

        if (Constants.ACTION_CREATE.equals(action)) {
            Card cardDraft = intent.getParcelableExtra(Constants.CARD);
            createCard(cardDraft);
        }
        else if (Intent.ACTION_SEND.equals(action)) {
            try {
                Card cardDraft = makeCardDraft(intent);
                createCard(cardDraft);
            } catch (Exception e) {
                view.showErrorMsg(R.string.CARD_EDIT_error_editing_card, e.getMessage());
                e.printStackTrace();
            }
        }
        else if (Intent.ACTION_EDIT.equals(action)) {
            String cardKey = intent.getStringExtra(Constants.CARD_KEY);
            editCard(cardKey);
        }
        else {
            throw  new Exception("Unknown intent action '"+action+"'");
        }
    }


    // Да варианта работы
    @Override
    public void createCard(final Card cardDraft) {
        Log.d(TAG, "createCard(), "+cardDraft);

        view.hideWating();
        view.enableForm();

        cardDraft.setKey(model.createKey());

        currentCard = cardDraft;

        String cardType = cardDraft.getType();

        switch (cardType) {

            case Constants.TEXT_CARD:
                view.prepareTextCardForm(cardDraft);
                break;

            case Constants.IMAGE_CARD:
                view.prepareImageCardForm(cardDraft);
                break;

            default:
                view.showErrorMsg(R.string.wrong_card_type);
                Log.d(TAG, cardType);
        }
    }

    @Override
    public void editCard(String cardKey) {
        Log.d(TAG, "editCard("+cardKey+")");

        model.loadCard(cardKey, new iCardsService.CardCallbacks() {
            @Override
            public void onLoadSuccess(Card card) {

                view.hideWating();
                view.enableForm();

                currentCard = card;
                oldTags = card.getTags();

                switch (card.getType()) {

                    case Constants.TEXT_CARD:
                        view.displayTextCard(card);
                        break;

                    case Constants.IMAGE_CARD:
                        view.displayImageCard(card);
                        break;

                    default:
                        view.showErrorMsg(R.string.wrong_card_type);
                        Log.e(TAG, "Unknown card type: "+card.getType());
                }
            }

            @Override
            public void onLoadFailed(String msg) {

            }

            // Убрать этот?
            @Override
            public void onLoadCanceled() {

            }

            // Не к месту эти
            @Override
            public void onDeleteSuccess(Card card) {

            }

            @Override
            public void onDeleteError(String msg) {

            }
        });
    }


    // Реакция на кнопки
    @Override
    public void onSaveButtonClicked() {
        Log.d(TAG, "saveButtonClicked()");

        if (null != localImageURI) {
            Log.d(TAG, "Новая картинка");

            try {
                String remoteImagePath = constructImagePath();
                view.disableForm();
                model.uploadImage(localImageURI, localImageType, remoteImagePath, this);

            } catch (Exception e) {
                view.showErrorMsg(R.string.image_data_error);
                e.printStackTrace();
            }

        } else {
            Log.d(TAG, "Старая картинка");
            try {
                saveCompleteCard();
            } catch (Exception e) {
                view.showErrorMsg(R.string.error_saving_card);
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCancelButtonClicked() {
        Log.d(TAG, "onCancelButtonClicked()");
        forgetCardData();
        model.cancelUpload();
        view.finishEdit(null);
    }

    @Override
    public void onSelectImageClicked() {
        Log.d(TAG, "onSelectImageClicked()");
        view.selectImage();
    }

    @Override
    public void onImageDiscardClicked() {
        Log.d(TAG, "onImageDiscardClicked()");
        model.cancelUpload();
        view.removeImage();
        currentCard.setImageURL(null);
    }

    @Override
    public void onAddTagButtonClicked() {
        Log.d(TAG, "onAddTagButtonClicked()");

        String newTag = view.getNewTag();

        if (!TextUtils.isEmpty(newTag)) {

            newTag = MyUtils.normalizeTag(newTag);

            HashMap<String,Boolean> existingTags = view.getCardTags();

            if (!existingTags.containsKey(newTag)) {
                view.addTag(newTag);
            }

            view.clearNewTag();
            view.focusTagInput();
        }
    }


    // Реакция на события
    @Override
    public void onImageSelected(Uri imageURI, String mimeType) {
        Log.d(TAG, "onImageSelected("+imageURI+", "+mimeType+")");

        localImageURI = imageURI;
        localImageType = mimeType;

        view.showImage(imageURI);
    }


    // Коллбеки выгрузки картинки
    // TODO: нормальная отмена выгрузки картинки...
    @Override
    public void onImageUploadProgress(int progress) {
        Log.d(TAG, "imageUploadProgress: "+progress);
    }

    @Override
    public void onImageUploadSuccess(Uri remoteImageURI) {
        Log.d(TAG, "onImageUploadSuccess(), "+remoteImageURI);

        newImageURI = remoteImageURI.toString();

        try {
            saveCompleteCard();
        } catch (Exception e) {
            view.showErrorMsg(R.string.error_saving_card);
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onImageUploadError(String message) {
        view.showErrorMsg(R.string.image_upload_error);
        view.enableForm();
        Log.e(TAG, message);
    }

    @Override
    public void onImageUploadCancel() {
        view.showErrorMsg(R.string.image_upload_cancelled);
    }


    // Служебные методы
    @Override
    public void linkView(iCardEdit.View view) {
//        Log.d(TAG, "linkView()");
        if (null == this.view) this.view = view;
    }
    @Override
    public void unlinkView() {
//        Log.d(TAG, "unlinkView()");
        this.view = null;
    }

    @Override
    public void linkModel(iCardsService model) {
//        Log.d(TAG, "linkModel()");
        this.model = model;
    }
    @Override
    public void unlinkModel() {
//        Log.d(TAG, "unlinkModel()");
        this.model = null;
    }


    // Колбеки сохранения карточки
    // TODO: проверка данных перед отправкой
    @Override
    public void onCardSaveSuccess(Card card) {
//        Log.d(TAG, "onCardSaveSuccess()");

        TagsSingleton.getInstance().updateCardTags(
                currentCard.getKey(),
                oldTags,
                newTags,
                null
        );

        forgetCardData();
        view.finishEdit(card);
    }

    @Override
    public void onCardSaveError(String message) {
        view.showErrorMsg(R.string.error_saving_card);
        view.enableForm();
        Log.e(TAG, message);
    }

    @Override
    public void onCardSaveCancel() {
        view.showErrorMsg(R.string.card_saving_cancelled);
        view.enableForm();
    }


    // TODO: единый метод обработки поступающих изображений

    // Внутренние методы
   private Card makeCardDraft(final Intent intent) throws Exception {
        Log.d(TAG, "makeCardDraft()");

        String type = intent.getType();

        if (type != null) {
            Card cardDraft = new Card();

            if ("text/plain".equals(type)) {
                String text = intent.getStringExtra(Intent.EXTRA_TEXT);
                cardDraft.setType(Constants.TEXT_CARD);
                cardDraft.setQuote(text);

            } else if (type.startsWith("image/")) {
                Uri imageURI = intent.getParcelableExtra(Intent.EXTRA_STREAM);

                localImageURI = imageURI;
                localImageType = MyUtils.getMimeTypeFromIntent(intent);

                cardDraft.setType(Constants.IMAGE_CARD);
                cardDraft.setImageURL(imageURI.toString());

            } else {
                throw new Exception("Unsupported intent content type '"+type+"'");
            }

            return cardDraft;
        }
        else {
            throw new Exception("Missing intent content type");
        }
    }


    private void saveCompleteCard() throws Exception {
        Log.d(TAG, "saveCompleteCard()");

        currentCard.setTitle(view.getCardTitle());
        currentCard.setDescription(view.getCardDescription());
        currentCard.setTags(view.getCardTags());

        Log.d(TAG, "CARD TO SAVE: "+currentCard);

        newTags = currentCard.getTags();

        switch (currentCard.getType()) {

            case Constants.TEXT_CARD:
                currentCard.setQuote(view.getCardQuote());
                break;

            case Constants.IMAGE_CARD:
                currentCard.setImageURL(newImageURI);
                break;

            default:
                view.showErrorMsg(R.string.wrong_card_type);
                throw new Exception("Unknown card type: "+currentCard.getType());
        }

        view.disableForm();
        model.updateCard(currentCard, this);
    }

    private String constructImagePath() throws Exception {
        String fname = currentCard.getKey();
        String fext = MyUtils.mime2ext(localImageType);

        if (null == fext) {
            view.showErrorMsg(R.string.wrong_mime_type);
            throw new Exception("Wrong mime type: "+localImageType);
        }

        return Constants.IMAGES_PATH + "/" + fname + "." + fext;
    }

    private void forgetCardData() {
        Log.d(TAG, "forgetCardData()");
        currentCard = null;
        localImageURI = null;
        localImageType = null;
        newImageURI = null;
    }

}