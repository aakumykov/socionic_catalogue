package ru.aakumykov.me.mvp.card;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.HashMap;

import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.interfaces.iAuthSingleton;
import ru.aakumykov.me.mvp.interfaces.iCardsSingleton;
import ru.aakumykov.me.mvp.interfaces.iStorageSingleton;
import ru.aakumykov.me.mvp.interfaces.iTagsSingleton;
import ru.aakumykov.me.mvp.models.Card;
import ru.aakumykov.me.mvp.services.AuthSingleton;
import ru.aakumykov.me.mvp.services.CardsSingleton;
import ru.aakumykov.me.mvp.services.StorageSingleton;
import ru.aakumykov.me.mvp.services.TagsSingleton;
import ru.aakumykov.me.mvp.utils.MVPUtils.MVPUtils;
import ru.aakumykov.me.mvp.utils.MyUtils;

public class CardEdit_Presenter implements
        iCardEdit.Presenter,
        iCardsSingleton.LoadCallbacks,
        iCardsSingleton.SaveCardCallbacks,
        iStorageSingleton.FileUploadCallbacks
{
    private final static String TAG = "CardEdit_Presenter";
    private iCardEdit.View view;
    private iCardsSingleton cardsService = CardsSingleton.getInstance();
    private iAuthSingleton authService = AuthSingleton.getInstance();
    private iStorageSingleton storageService = StorageSingleton.getInstance();
    private iTagsSingleton tagsService = TagsSingleton.getInstance();

    private Card currentCard = null;
    private HashMap<String,Boolean> oldTags = null;
    private HashMap<String,Boolean> newTags = null;


    // Интерфейсные методы
    @Override
    public void chooseStartVariant(@Nullable Intent intent) {
        if (null == intent) {
            view.showErrorMsg(R.string.CARD_EDIT_error_no_input_data);
            return;
        }

        String action = intent.getAction();

        switch (action+"") {

            case Constants.ACTION_CREATE:
                try {
                    prepareCardCreation();
                } catch (Exception e) {
                    view.showErrorMsg(R.string.CARD_EDIT_error_creating_card, e.getMessage());
                    e.printStackTrace();
                }
                break;

            case Intent.ACTION_SEND:
                try {
                    prepareCardCreation();
                    processRecievedData(Constants.MODE_SEND, intent);
                } catch (Exception e) {
                    view.showErrorMsg(R.string.CARD_EDIT_error_creating_card, e.getMessage());
                    e.printStackTrace();
                }
                break;

            case Constants.ACTION_EDIT:
                try {
                    prepareCardEdition(intent);
                } catch (Exception e) {
                    view.showErrorMsg(R.string.CARD_EDIT_error_editing_card, e.getMessage());
                    e.printStackTrace();
                }
                break;

            default:
                view.showErrorMsg(R.string.CARD_EDIT_error_unknown_action);
        }
    }

    @Override
    public void processRecievedData(String mode, Intent intent) throws Exception {

        String inputDataMode = MVPUtils.detectInputDataMode(intent);

        switch (inputDataMode) {

            case Constants.TYPE_TEXT:
                currentCard.setType(Constants.TEXT_CARD);
                procesIncomingText(intent);
                break;

            case Constants.TYPE_IMAGE_LINK:
                currentCard.setType(Constants.IMAGE_CARD);
                processLinkToImage(intent);
                break;

            case Constants.TYPE_IMAGE_DATA:
                currentCard.setType(Constants.IMAGE_CARD);
                processIncomingImage(intent);
                break;

            case Constants.TYPE_YOUTUBE_VIDEO:
                currentCard.setType(Constants.VIDEO_CARD);
                processYoutubeVideo(intent);
                break;

            default:
                view.showErrorMsg(R.string.CARD_EDIT_unknown_data_mode);
        }
    }

    @Override
    public void processLinkToImage(Intent intent) throws Exception {
        String textLinkToImage = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (null == textLinkToImage) {
            throw new Exception("No EXTRA_TEXT in intent");
        }

        currentCard.setImageURL("");
        view.displayImage(textLinkToImage, true);
    }

    @Override
    public void processIncomingImage(@Nullable Intent intent) throws Exception {

        if (null == intent) {
            throw new Exception("Intent is null");
        }

        // Первый способ получить содержимое
        Uri imageURI = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (null == imageURI) {

            // Второй способ получить содержимое
            imageURI = intent.getData();
            if (null == imageURI) {
                throw new Exception("Where is no image data in intent");
            }
            view.showLongToast("imageURI получен вторым способом: "+imageURI);
        }

        currentCard.setImageURL("");
        view.displayImage(imageURI.toString(), true);
    }

    // TODO: как бы проверять полную корректность при сохранении?
    @Override
    public void saveCard() throws Exception {

        if (null == currentCard.getType()) {
            view.showErrorMsg(R.string.CARD_EDIT_select_card_type);
            return;
        }

        view.disableForm();
        view.showProgressBar();

        currentCard.setTitle(view.getCardTitle());
        currentCard.setDescription(view.getCardDescription());
         newTags = view.getCardTags(); // Новые метки сохраняются для последующего обновления БД
        currentCard.setTags(newTags);

        if (currentCard.isTextCard()) {
            currentCard.setQuote(view.getCardQuote());
        }

        if (currentCard.isVideoCard()) {
            String videoCode = view.getCardVideoCode();
            currentCard.setVideoCode(videoCode);
        }

        if (currentCard.isImageCard() && !currentCard.hasImageURL()) {

            /* Если картинка была изменена, imageURL в currentCard стирается,
             * а после отправки картинки на сервер, устанавливается.
             * При повторном вызове, ориентируясь на это, выгрузка картинки будет пропущена.*/

            if (TextUtils.isEmpty(currentCard.getImageURL())) {
                // Здесь сохраняется изображение

                String fileName = currentCard.getKey()+".jpg";
                view.showInfoMsg(R.string.CARD_EDIT_uploading_image);
                view.showImageProgressBar();
                view.disableForm();

                try {
                    storageService.uploadImage(view.getImageData(), fileName, this);
                    return;

                } catch (Exception e) {
                    view.hideImageProgressBar();
                    view.enableForm();

                    view.showErrorMsg(R.string.CARD_EDIT_error_saving_image, e.getMessage());
                    e.printStackTrace();
                    return;
                }
            }
        }

        // Сохранение собственно карточки
        view.showInfoMsg(R.string.CARD_EDIT_saving_card);
        cardsService.updateCard(currentCard, this);
    }



    @Override
    public void setCardType(String cardType) {
        String[] availableCardTypes = {
                Constants.TEXT_CARD,
                Constants.IMAGE_CARD,
                Constants.VIDEO_CARD
        };

        if (Arrays.asList(availableCardTypes).contains(cardType)) {
            currentCard.setType(cardType);
        } else {
            throw new IllegalArgumentException("Unknown card type '"+cardType+"'");
        }
    }

    @Override
    public String processNewTag(String tagName) {

//        if (!TextUtils.isEmpty(tagName)) {
            tagName = MVPUtils.normalizeTag(tagName);
            return tagName;
//            if (!view.getCardTags().containsKey(tagName)) {
//                return tagName;
//            }
//        }
//
//        return null;
    }


    // Обязательные методы
    @Override
    public void linkView(iCardEdit.View view) {
        this.view = view;
    }

    @Override
    public void unlinkView() {
        this.view = null;
    }


    // Коллбеки
    // --Загрузки карточки
    @Override
    public void onCardLoadSuccess(final Card card) {
        currentCard = card;
        oldTags = card.getTags();

        view.hideProgressBar();
        view.displayCard(card);
    }

    @Override
    public void onCardLoadFailed(String msg) {
        currentCard = null;
        view.hideProgressBar();
        view.showErrorMsg(R.string.CARD_EDIT_error_loading_card);
    }

    // --Сохранение карточки
    @Override
    public void onCardSaveSuccess(Card card) {

        TagsSingleton.getInstance().updateCardTags(
                currentCard.getKey(),
                oldTags,
                newTags,
                null
        );

        view.hideProgressBar();
        view.finishEdit(card);
    }

    @Override
    public void onCardSaveError(String message) {
        view.hideProgressBar();
        view.enableForm();
    }

    // --Отправки изображения
    @Override
    public void onFileUploadProgress(int progress) {
        view.setImageUploadProgress(progress);
    }

    @Override
    public void onFileUploadSuccess(String downloadURL) {
        view.hideImageProgressBar();

        currentCard.setImageURL(downloadURL);

        try {
            saveCard();
        } catch (Exception e) {
            view.showErrorMsg(R.string.CARD_EDIT_error_saving_card, e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onFileUploadFail(String errorMsg) {
        view.hideImageProgressBar();
        view.enableForm();
        view.showErrorMsg(R.string.CARD_EDIT_error_saving_image, errorMsg);
    }

    @Override
    public void onFileUploadCancel() {
        view.hideImageProgressBar();
        view.enableForm();
        view.showErrorMsg(R.string.CARD_EDIT_image_upload_cancelled);
    }


    // Внутренние методы
    private void prepareCardCreation() {

        view.setPageTitle(R.string.CARD_EDIT_card_creation_title);
        view.showModeSwitcher();

        currentCard = new Card();
        currentCard.setKey(cardsService.createKey());
        // TODO: нужно единообразие аутентификационных вещей!
        currentCard.setUserId(authService.currentUserId());
    }

    private void prepareCardEdition(Intent intent) {

        view.showProgressBar();
        view.setPageTitle(R.string.CARD_EDIT_card_edition_title);

        String cardKey = intent.getStringExtra(Constants.CARD_KEY);
        if (null == cardKey) {
            throw new IllegalArgumentException("There is no CARD_KEY in intent.");
        }

        cardsService.loadCard(cardKey, this);
    }

    private void procesIncomingText(Intent intent) throws Exception {

        String text = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (null == text) {
            throw new IllegalArgumentException("Intent.EXTRA_TEXT is null.");
        }

        String autoTitle = MyUtils.cutToLength(text, Constants.TITLE_MAX_LENGTH);

        view.hideProgressBar();
        view.displayTitle(autoTitle);
        view.displayQuote(text);
    }

    private void processYoutubeVideo(Intent intent) throws Exception {

        String link = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (null == link) {
            throw new IllegalArgumentException("Video link is null");
        }

        String videoCode = MVPUtils.extractYoutubeVideoCode(link);
        if (null == videoCode) {
            throw new IllegalArgumentException("Where is no video code in link '"+link+"");
        }

        view.displayVideo(videoCode);
    }

    private String makeRemoteFileName() throws Exception {

        String fname = currentCard.getKey();
        if (null == fname) {
            throw new Exception("There is no file name.");
        }

        String fext = MyUtils.mime2ext(currentCard.getMimeType());
        if (null == fext) {
            throw new Exception("There is no file extension.");
        }

        return fname + "." + fext;
    }


}
