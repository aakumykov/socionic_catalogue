package ru.aakumykov.me.sociocat.card_edit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;
import ru.aakumykov.me.insertable_yotube_player.InsertableYoutubePlayer;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_edit.view_model.CardEdit_ViewModel;
import ru.aakumykov.me.sociocat.card_edit.view_model.CardEdit_ViewModel_Factory;
import ru.aakumykov.me.sociocat.constants.Constants;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.utils.ImageLoader;
import ru.aakumykov.me.sociocat.utils.ImageType;
import ru.aakumykov.me.sociocat.utils.ImageUtils;
import ru.aakumykov.me.sociocat.utils.MVPUtils.MVPUtils;
import ru.aakumykov.me.sociocat.utils.MyUtils;
import ru.aakumykov.me.sociocat.utils.my_dialogs.MyDialogs;
import ru.aakumykov.me.sociocat.utils.my_dialogs.iMyDialogs;
import ru.aakumykov.me.sociocat.z_base_view.BaseView;

//@RuntimePermissions
public class CardEdit_View extends BaseView implements
        iCardEdit.View,
        TagView.OnTagClickListener
{
    @BindView(R.id.scrollView) ScrollView scrollView;

    @BindView(R.id.titleInput) EditText titleInput;
    @BindView(R.id.quoteInput) EditText quoteInput;
    @BindView(R.id.quoteSourceInput) EditText quoteSourceInput;
    @BindView(R.id.descriptionInput) EditText descriptionInput;

    @BindView(R.id.mediaThrobber) ProgressBar mediaThrobber;

    @BindView(R.id.imageHolder) ConstraintLayout imageHolder;
    @BindView(R.id.imageProgressBar) ProgressBar imageThrobber;
    @BindView(R.id.mediaView) ImageView imageView;
    @BindView(R.id.restoreImageButton) ImageView restoreImageButton;
    @BindView(R.id.discardImageButton) ImageView discardImageButton;

    @BindView(R.id.mediaPlayerHolder) LinearLayout mediaPlayerHolder;
    @BindView(R.id.playerContainer) FrameLayout playerContainer;
    @BindView(R.id.addMediaButtonContainer) FrameLayout addMediaButtonContainer;
    @BindView(R.id.addMediaButton) Button addMediaButton;
    @BindView(R.id.removeMediaButton) Button removeMediaButton;
    @BindView(R.id.convertToAudioButton) Button convertToAudioButton;
    @BindView(R.id.convertToVideoButton) Button convertToVideoButton;

    @BindView(R.id.timecodeControlsContainer) LinearLayout timecodeControlsContainer;
    @BindView(R.id.timecodeInput) EditText timecodeInput;
    @BindView(R.id.getTimecodeButton) ImageButton getTimecodeButton;
    @BindView(R.id.setTimecodeButton) ImageButton setTimecodeButton;

    @BindView(R.id.canonicalTagsContainer) TagContainerLayout tagsContainer;
    @BindView(R.id.newTagInput) AutoCompleteTextView newTagInput;
    @BindView(R.id.addTagButton) Button addTagButton;

    @BindView(R.id.saveButton) Button saveButton;
    @BindView(R.id.cancelButton) Button cancelButton;

    private final static String TAG = "CardEdit_View";

    private InsertableYoutubePlayer insertableYoutubePlayer;

    private iCardEdit.Presenter presenter;
    private final List<String> tagsList = new ArrayList<>();
    // TODO: место этих флагов - в презентере!
    private boolean isImageSelectionMode = false;
    private boolean imageIsSetted = false;
    private boolean exitIsExpected = false;
    private boolean selectImageMode = false;
    private boolean loginRequestMode = false;


    // Системные методы
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_edit_activity);
        ButterKnife.bind(this);

        setPageTitle(R.string.CARD_EDIT_page_title);
        activateUpButton();

        CardEdit_ViewModel cardEditViewModel = new ViewModelProvider(this, new CardEdit_ViewModel_Factory()).get(CardEdit_ViewModel.class);
        if (cardEditViewModel.hasPresenter())
            presenter = cardEditViewModel.getPresenter();
        else {
            presenter = new CardEdit_Presenter();
            cardEditViewModel.storePresenter(presenter);
        }

        tagsContainer.setOnTagClickListener(this);

        loadTagsAutocompleteList();
        setupTagWatcher();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        presenter.linkView(this);

        switch (requestCode) {

            case Constants.CODE_LOGIN_REQUEST:
                processLoginRequest(resultCode, data);
                break;

            case ImageUtils.CODE_SELECT_IMAGE:
                processImageSelection(resultCode, data);
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.linkView(this);

        if (isImageSelectionMode) {
            isImageSelectionMode = false;
            return;
        }

        if (loginRequestMode || !presenter.hasCard()) {
            loginRequestMode = false;
            presenter.onFirstOpen(getIntent());
        }
        else
            presenter.onConfigurationChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
/*        if (!exitIsExpected && !selectImageMode) {
            if (isFormFilled()) {
                try {
                    if (null != insertableYoutubePlayer)
                        insertableYoutubePlayer.pause();
                    presenter.saveEditState();
                } catch (Exception e) {
                    //showLongToast(R.string.CARD_EDIT_error_saving_edit_state);
                    showErrorMsg(R.string.CARD_EDIT_error_saving_edit_state, e.getMessage());
                    MyUtils.printError(TAG, e);
                }
            }
        }*/
        presenter.onViewPaused();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (null != insertableYoutubePlayer)
//            insertableYoutubePlayer.pause();
//
//        if (!selectImageMode) {
//            try {
//                presenter.restoreEditState();
//            } catch (Exception e) {
//                showErrorMsg(R.string.CARD_EDIT_error_restoring_edit_state, e.getMessage());
//                MyUtils.printError(TAG, e);
//            }
//        }

        presenter.onViewResumed();
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.unlinkView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != insertableYoutubePlayer)
            insertableYoutubePlayer.release();
    }

    @Override
    public void onUserGloballyLoggedIn() {

    }

    @Override
    public void onUserGloballyLoggedOut() {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onCancelClicked();
                return true;
            case R.id.actionSave:
                onSaveClicked();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
//        super.onCancelRequested(); // Если не закомментировать, выходит, игнорируя диалог
        onCancelClicked();
    }

    @Override
    public void showErrorMsg(int messageId, String consoleMessage) {
        super.showErrorMsg(messageId, consoleMessage);
        enableForm();
        MyUtils.hide(mediaThrobber);
        MyUtils.hide(imageThrobber);
        scrollView.scrollTo(0,0);
    }


    @Override
    public void requestLogin(Intent transitIntent) {
        loginRequestMode = true;
        super.requestLogin(transitIntent);
    }

    // Интерфейсные методы
    @Override
    public void displayCard(Card card, boolean omitImage) {

        hideProgressMessage();

        String cardType = card.getType();

        switch (cardType) {
            case Constants.TEXT_CARD:
                displayQuote(card.getQuote(), card.getQuoteSource());
                break;
            case Constants.IMAGE_CARD:
                if (!omitImage)
                    displayImage(card.getImageURL());
                break;
            case Constants.VIDEO_CARD:
                displayVideo(card.getVideoCode(), card.getTimecode());
                break;
            case Constants.AUDIO_CARD:
                displayAudio(card.getAudioCode(), card.getTimecode());
                break;
            default:
                showErrorMsg(R.string.wrong_card_type, "Unknown card type: "+cardType);
        }

        displayCommonCardParts(card);

        enableForm();
    }
    
    @Override
    public void pickImage() {
        if (! ImageUtils.pickImage(this) )
            showErrorMsg(R.string.error_selecting_image, "Cannot launch file selector");
        else
            isImageSelectionMode = true;
    }
    
    @Override
    public <T> void displayImage(T imageData) {

        MyUtils.show(imageHolder);
        showImageThrobber();

        ImageLoader.loadImage(this, imageData, new ImageLoader.LoadImageCallbacks() {
            @Override
            public void onImageLoadSuccess(Bitmap imageBitmap) {
                imageView.setImageBitmap(imageBitmap);
                imageIsSetted = true;

                hideImageThrobber();
                hideImageError();

                MyUtils.show(discardImageButton);
                MyUtils.hide(restoreImageButton);
            }

            @Override
            public void onImageLoadError(String errorMsg) {
                imageView.setImageResource(R.drawable.ic_image_error);
                imageIsSetted = false;

                Log.e(TAG, errorMsg);

                hideImageThrobber();
                showImagePlaceholder();MyUtils.show(imageHolder);

//                showErrorMsg(R.string.CARD_EDIT_error_displaying_image, errorMsg);
            }
        });
    }

    @Override
    public void displayVideo(final String youtubeCode, @Nullable Float timecode) {

        if (TextUtils.isEmpty(youtubeCode)) {
            MyUtils.show(addMediaButton);
            return;
        }

        removeMediaButton.setText(R.string.CARD_EDIT_remove_video);

        prepareMediaPlayer();

        activateMediaPlayer(InsertableYoutubePlayer.PlayerType.VIDEO_PLAYER, youtubeCode, timecode);
    }

    @Override
    public void displayAudio(final String youtubeCode, @Nullable Float timecode) {

        if (null == youtubeCode) {
            MyUtils.show(addMediaButton);
            return;
        }

        removeMediaButton.setText(R.string.CARD_EDIT_remove_video);

        prepareMediaPlayer();

        activateMediaPlayer(InsertableYoutubePlayer.PlayerType.AUDIO_PLAYER, youtubeCode, timecode);
    }

    @Override
    public void removeImage() {
        imageIsSetted = false;
        showImagePlaceholder();
        MyUtils.hide(discardImageButton);
        MyUtils.show(restoreImageButton);
    }

    @Override
    public boolean hasImage() {
        return imageIsSetted;
    }

    @Override
    public void removeMedia() {
        insertableYoutubePlayer.remove();

        MyUtils.show(addMediaButton);

        MyUtils.hide(removeMediaButton);
        MyUtils.hide(convertToVideoButton);
        MyUtils.hide(convertToAudioButton);
    }

    @Override
    public String getCardTitle() {
        return titleInput.getText().toString();
    }

    @Override
    public String getQuote() {
        return quoteInput.getText().toString();
    }

    @Override
    public String getQuoteSource() {
        return quoteSourceInput.getText().toString();
    }

    @Override
    public String getDescription() {
        return descriptionInput.getText().toString();
    }

    @Override
    public Float getTimecode() {
        String timecodeString = timecodeInput.getText().toString();

        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);
                   dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            Date date = dateFormat.parse(timecodeString);
            long seconds = date.getTime() / 1000L;
            return seconds * 1.0f;
        }
        catch (ParseException e) {
            Log.e(TAG, e.getMessage());
            showToast(R.string.CARD_EDIT_error_parsing_timecode);
            MyUtils.printError(TAG, e);
            return 0.0f;
        }
    }

    @Override
    public HashMap<String,Boolean> getTags() {
        List<String> tagsList = tagsContainer.getTags();
        HashMap<String,Boolean> tagsMap = new HashMap<>();
        for(int i=0; i<tagsList.size(); i++)
            tagsMap.put(tagsList.get(i), true);
        return tagsMap;
    }

    @Override
    public void convert2audio() {
        insertableYoutubePlayer.convert2audio();
        changeButtonsForAudio();
    }

    @Override
    public void convert2video() {
        insertableYoutubePlayer.convert2video();
        changeButtonsForVideo();
    }

    @Override
    public void showTitleError(int msgId) {
        showErrorAndFocus(msgId, titleInput);
    }

    @Override
    public void showQuoteError(int msgId) {
        showErrorAndFocus(msgId, quoteInput);
    }

    @Override
    public void showVideoError(int msgId) {
        showToast(msgId);
    }

    @Override
    public void showAudioError(int msgId) {
        showToast(msgId);
    }

    @Override
    public void showMediaError() {
        addMediaButtonContainer.setBackgroundResource(R.drawable.shape_red_border);
    }

    @Override
    public void hideMediaError() {
        addMediaButtonContainer.setBackground(null);
    }

    @Override
    public void showDescriptionError(int msgId) {
        showErrorAndFocus(msgId, descriptionInput);
    }

    @Override
    public void showImageError(int msgId) {
        showToast(msgId);

        Drawable redBorderBackground = getResources().getDrawable(R.drawable.shape_image_error);
        imageHolder.setBackground(redBorderBackground);
    }

    @Override
    public void hideImageError() {
        imageHolder.setBackground(null);
    }

    @Override
    public void disableForm() {
        changeFormSate(false);
        tagsContainer.setOnTagClickListener(null);
    }

    @Override
    public void enableForm() {
        changeFormSate(true);
        tagsContainer.setOnTagClickListener(this);
    }

    @Override
    public void showImageThrobber() {
        MyUtils.show(imageThrobber);
        imageView.setAlpha(0.5f);
    }

    @Override
    public void hideImageThrobber() {
        MyUtils.hide(imageThrobber);
        imageView.setAlpha(1.0f);
    }

    @Override
    public boolean isFormFilled() {
        boolean changed = false;

        if (!TextUtils.isEmpty(getCardTitle())) changed = true;
        if (!TextUtils.isEmpty(getQuote())) changed = true;
        if (!TextUtils.isEmpty(getQuoteSource())) changed = true;
        if (!TextUtils.isEmpty(getDescription())) changed = true;
        if (tagsContainer.getTags().size() > 0) changed = true;
        if (null != insertableYoutubePlayer && insertableYoutubePlayer.hasMedia()) changed = true;

        return changed;
    }

    @Override
    public void finishEdit(Card card) {
        exitIsExpected = true;

        Intent intent = new Intent();
        intent.putExtra(Constants.CARD, card);
        setResult(RESULT_OK, intent);

        finish();
    }

    @Override
    public void addTag(String tag) {
        tagsContainer.addTag(tag);
        newTagInput.setText("");
    }

    @Override
    public void focusFirstField(boolean launchKeyboard) {
        if (launchKeyboard)
            MyUtils.showKeyboardOnFocus(this, titleInput);
        else
            titleInput.requestFocus();
    }

    @Override
    public void prepareForTextCard(String title, String quote) {
        titleInput.setText(title);
        quoteInput.setText(quote);

        MyUtils.show(quoteInput);
        MyUtils.show(quoteSourceInput);
    }

    @Override
    public void prepareForImageCard(@Nullable Bitmap imageBitmap) {
        if (null != imageBitmap) {
            imageIsSetted = true;
            imageView.setImageBitmap(imageBitmap);
        }
        else
            showImagePlaceholder();

        MyUtils.show(imageHolder);
        MyUtils.show(quoteSourceInput);
    }

    @Override
    public void prepareForVideoCard(@Nullable String videoCode, @Nullable String timeCode) {
        if (null != videoCode)
            displayVideo(videoCode, null);
        else
            MyUtils.show(addMediaButton);
    }

    @Override
    public void prepareForAudioCard(@Nullable String audioCode, @Nullable Float timeCode) {
        if (null != audioCode)
            displayAudio(audioCode, null);
        else
            MyUtils.show(addMediaButton);
    }

    @Override
    public float pauseMedia() {
        if (null != insertableYoutubePlayer) {
            float position = insertableYoutubePlayer.getPosition();
            insertableYoutubePlayer.pause();
            return position;
        }
        return 0.0f;
    }

    @Override
    public void resumeMedia(float position) {
        if (null != insertableYoutubePlayer) {
            insertableYoutubePlayer.play(position);
        }
    }

   


    // Нажатия
    @OnClick(R.id.mediaView)
    public void onSelectImageClicked() {
        presenter.onImageViewClicked();
    }

    @OnClick(R.id.discardImageButton)
    void onResetImageClicked() {
        presenter.removeImageClicked();
    }

    @OnClick(R.id.restoreImageButton)
    void onRestoreImageButtonClicked() {
        presenter.restoreImageClicked();
    }

    @OnClick(R.id.addMediaButton)
    void onAddMediaClicked() {

        MyDialogs.stringInputDialog(
                this,
                R.string.CARD_EDIT_add_youtube_link,
                null,
                R.string.CARD_EDIT_paste_youtube_link,
                new iMyDialogs.StringInputCallback() {

                    @Override public String onPrepareText() {
                        String clipbText = MyUtils.getClipboardText(CardEdit_View.this);
                        if (MVPUtils.isYoutubeLink(clipbText))
                            return clipbText;
                        else
                            return null;
                    }

                    @Override
                    public String onYesClicked(String inputtedString) {
                        String youtubeCode = MVPUtils.extractYoutubeVideoCode(inputtedString);
                        if (null == youtubeCode) {
                            return getResources().getString(R.string.CARD_EDIT_wrong_youtube_code);
                        } else {
                            return null;
                        }
                    }

                    @Override
                    public void onSuccess(String inputtedString) {
                        try {
                            hideMediaError();
                            MyUtils.hide(addMediaButton);
                            presenter.processYoutubeLink(inputtedString);
                        } catch (Exception e) {
                            showErrorMsg(R.string.CARD_EDIT_error_processing_data, e.getMessage());
                            MyUtils.printError(TAG, e);
                        }
                    }
                });
    }

    @OnClick(R.id.removeMediaButton)
    void onRemoveMediaClicked() {
        presenter.removeMediaClicked();
    }

    @OnClick(R.id.getTimecodeButton)
    void onGetTimecodeButtonClicked() {
        getTimecodeFromVideo();
    }

    @OnClick(R.id.setTimecodeButton)
    void onSetTimecodeButtonClicked() {
        setTimecodeToVideo();
    }

    @OnClick(R.id.convertToAudioButton)
    void onConvertToAudioClicked() {
        presenter.convert2audio();
    }

    @OnClick(R.id.convertToVideoButton)
    void onConvertToVideoClicked() {
        presenter.convert2video();
    }

    @OnClick(R.id.addTagButton)
    void onAddTagClicked() {
        presenter.processTag(newTagInput.getText().toString());
    }

    @OnClick(R.id.saveButton)
    void onSaveClicked() {
        final String tag = newTagInput.getText().toString();

        if (TextUtils.isEmpty(tag)) {
            saveCardReal();
            return;
        }

        MyDialogs.forgottenTagDialog(
                this,
                getString(R.string.CARD_EDIT_forgotten_tag_dialog_message, tag),
                new iMyDialogs.StandardCallbacks() {
                    @Override
                    public void onCancelInDialog() {
                        saveCardReal();
                    }

                    @Override
                    public void onNoInDialog() {
                        saveCardReal();
                    }

                    @Override
                    public boolean onCheckInDialog() {
                        return true;
                    }

                    @Override
                    public void onYesInDialog() {
                        presenter.processTag(tag);
                        saveCardReal();
                    }
                });
    }

    @OnClick(R.id.cancelButton)
    void onCancelClicked() {
        if (isFormFilled()) {
            MyDialogs.cancelEditDialog(
                    this,
                    R.string.CARD_EDIT_cancel_editing_title,
                    R.string.CARD_EDIT_cancel_editing_message,
                    new iMyDialogs.StandardCallbacks() {
                        @Override
                        public void onCancelInDialog() {

                        }

                        @Override
                        public void onNoInDialog() {

                        }

                        @Override
                        public boolean onCheckInDialog() {
                            return true;
                        }

                        @Override
                        public void onYesInDialog() {
                            //clearSharedPrefs(getSharedPrefs(Constants.SHARED_PREFERENCES_CARD_EDIT), Constants.CARD);
                            presenter.clearEditState();
                            gracefulExit();
                        }
                    }
            );
        }
        else {
            gracefulExit();
        }
    }


    // Методы обратнаго вызова
    @Override
    public void onTagClick(int position, String text) {

    }

    @Override
    public void onTagLongClick(int position, String text) {

    }

    @Override
    public void onSelectedTagDrag(int position, String text) {

    }

    @Override
    public void onTagCrossClick(int position) {
        tagsContainer.removeTag(position);
    }


    // Внутренние методы
    private void processLoginRequest(int resultCode, @Nullable Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                startEditWork(data);
                break;

            case RESULT_CANCELED:
                setResult(RESULT_CANCELED);
                finish();
                break;

            default:
                showErrorMsg(R.string.error_unknown_result_code, "Unknown result code: "+resultCode);
                break;
        }
    }

    private void processImageSelection(int resultCode, @Nullable Intent data) {
        selectImageMode = false;

        switch (resultCode) {
            case RESULT_OK:
                processSelectedImage(data);
                break;
            case RESULT_CANCELED:
                    break;
            default:
                showErrorMsg(R.string.error_selecting_image, "Unknown result code");
        }
    }

    private void processSelectedImage(@Nullable Intent data) {
        try {
            ImageUtils.extractImageFromIntent(this, data, new ImageUtils.ImageExtractionCallbacks() {
                @Override
                public void onImageExtractionSuccess(Bitmap bitmap, ImageType imageType) {
                    presenter.onImageSelectionSuccess(bitmap, imageType);
                }

                @Override
                public void onImageExtractionError(String errorMsg) {
                    presenter.onImageSelectionError(errorMsg);
                }
            });
        }
        catch (ImageUtils.ImageUtils_Exception e) {
            presenter.onImageSelectionError(e.getMessage());
            MyUtils.printError(TAG, e);
        }
    }

    private void loadTagsAutocompleteList() {
        presenter.loadTagsList(new iCardEdit.TagsListLoadCallbacks() {
            @Override
            public void onTagsListLoadSuccess(List<String> list) {
                tagsList.addAll(list);
                configureTagAutocomplete();
            }

            @Override
            public void onTagsListLoadFail(String errorMsg) {
                showErrorMsg(R.string.CARD_EDIT_error_loading_tags_list, errorMsg);
            }
        });
    }

    private void startEditWork(Intent intent) {
        /*try {
            presenter.onFirstOpen(intent);


        }
        catch (Exception e) {
            showErrorMsg(R.string.CARD_EDIT_error_editing_card, e.getMessage());
            MyUtils.printError(TAG, e);
        }*/
    }

    private void configureTagAutocomplete() {
        newTagInput.setThreshold(1);

        TagAutocompleteAdapter tagAutocompleteAdapter = new TagAutocompleteAdapter(
                this,
                R.layout.tag_autocomplete_item,
                tagsList
        );

        newTagInput.setAdapter(tagAutocompleteAdapter);

        newTagInput.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                addTag(tagsList.get(position));
                newTagInput.setText("");
            }
        });
    }

    private void displayQuote(String... quoteParts) {
        quoteInput.setText(quoteParts[0]);
        if (2 == quoteParts.length)
            quoteSourceInput.setText(quoteParts[1]);

        MyUtils.show(quoteInput);
    }

    private void displayCommonCardParts(Card card) {
        titleInput.setText(card.getTitle());

        descriptionInput.setText(card.getDescription());

        tagsContainer.setTags(card.getTags());

        quoteSourceInput.setText(card.getQuoteSource());
        MyUtils.show(quoteSourceInput);
    }

    private void showImagePlaceholder() {
        imageView.setImageResource(R.drawable.ic_add_image);
        MyUtils.show(imageHolder);
    }

    private void changeFormSate(boolean isEnabled) {

        titleInput.setEnabled(isEnabled);
        quoteInput.setEnabled(isEnabled);
        quoteSourceInput.setEnabled(isEnabled);

        imageView.setEnabled(isEnabled);
        discardImageButton.setEnabled(isEnabled);
        restoreImageButton.setEnabled(isEnabled);

        addMediaButton.setEnabled(isEnabled);
        removeMediaButton.setEnabled(isEnabled);

        convertToVideoButton.setEnabled(isEnabled);
        convertToAudioButton.setEnabled(isEnabled);

        newTagInput.setEnabled(isEnabled);
        addTagButton.setEnabled(isEnabled);
        descriptionInput.setEnabled(isEnabled);

        saveButton.setEnabled(isEnabled);
    }

    private void gracefulExit() {
        exitIsExpected = true;

        // TODO: удалять аудио/видео

        setResult(RESULT_CANCELED);
        finish();
    }

    private void setupTagWatcher() {

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                int commaIndex = text.indexOf(",");
                if (commaIndex > -1) {
                    String tag = text.substring(0, commaIndex);
                    presenter.processTag(tag);
                    String restText = text.substring(commaIndex+1);
                    newTagInput.setText(restText);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        newTagInput.addTextChangedListener(textWatcher);
    }

    private void saveCardReal() {
        try {
            presenter.saveCard(false);
        } catch (Exception e) {
            showErrorMsg(R.string.CARD_EDIT_error_saving_card, e.getMessage());
            MyUtils.printError(TAG, e);
        }
    }

    private void prepareMediaPlayer() {
        if (null != insertableYoutubePlayer)
            return;

        insertableYoutubePlayer = new InsertableYoutubePlayer(
                this,
                playerContainer,
                R.string.CARD_EDIT_preparing_player
        );

        addMediaButton.setText(R.string.CARD_EDIT_add_youtube_link);
    }

    private void activateMediaPlayer(InsertableYoutubePlayer.PlayerType playerType, final String youtubeCode, @Nullable Float timecode) {

        insertableYoutubePlayer
                .addOnAppearListener(new InsertableYoutubePlayer.iAppearListener() {
                    @Override
                    public void onAppear() {
                        showTimecodeInput(timecode);
                        timecodeInput.setText(MyUtils.seconds2HHMMSS(timecode));
                        MyUtils.show(convertToAudioButton);
                        MyUtils.show(removeMediaButton);
                    }
                })
                .addOnReadyListener(new InsertableYoutubePlayer.iReadyListener() {
                    @Override
                    public void onReady(Float duration) {
                        enableTimecodeInput();
                    }
                })
                .addOnSeekListener(new InsertableYoutubePlayer.iSeekListener() {
                    @Override
                    public void onSeek(float timecode) {
                        setTimecode(timecode);
                    }
                });

        insertableYoutubePlayer.show(youtubeCode, 0.0f, playerType);
    }

    private void changeButtonsForVideo() {
        MyUtils.show(convertToAudioButton);
        MyUtils.hide(convertToVideoButton);
        removeMediaButton.setText(R.string.CARD_EDIT_remove_video);
    }

    private void changeButtonsForAudio() {
        MyUtils.show(convertToVideoButton);
        MyUtils.hide(convertToAudioButton);
        removeMediaButton.setText(R.string.CARD_EDIT_remove_audio);
    }

    private void showTimecodeInput(Float timecode) {
        timecodeInput.setText(MyUtils.seconds2HHMMSS(timecode));

        MyUtils.show(timecodeControlsContainer);
    }

    private void setTimecode(Float position) {
        String timecodeString = MyUtils.seconds2HHMMSS(position);
        timecodeInput.setText(timecodeString);
    }

    private void enableTimecodeInput() {
        MyUtils.enable(timecodeInput);
        MyUtils.enable(getTimecodeButton);
        MyUtils.enable(setTimecodeButton);
    }

    private void getTimecodeFromVideo() {
        float mVideoPosition = insertableYoutubePlayer.getPosition();
        String timecodeString = MyUtils.seconds2HHMMSS(mVideoPosition);
        timecodeInput.setText(timecodeString);
    }

    private void setTimecodeToVideo() {
        float timecode = getTimecode();
        insertableYoutubePlayer.seekTo(timecode);
    }

    private void showErrorAndFocus(int msgId, EditText editText) {
        editText.setError(getResources().getString(msgId));

        Rect offsetViewBounds = new Rect();
        editText.getDrawingRect(offsetViewBounds);
        scrollView.offsetDescendantRectToMyCoords(editText, offsetViewBounds);

        int relativeTop = offsetViewBounds.top;
//        int relativeLeft = offsetViewBounds.left;

        scrollView.smoothScrollTo(0, relativeTop);
        editText.requestFocus();
    }
}
