package ru.aakumykov.me.sociocat.card_edit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.SeekBar;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;
import ru.aakumykov.me.insertable_yotube_player.InsertableYoutubePlayer;
import ru.aakumykov.me.sociocat.BaseView;
import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_show.CardShow_View;
import ru.aakumykov.me.sociocat.interfaces.iMyDialogs;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.utils.MVPUtils.MVPUtils;
import ru.aakumykov.me.sociocat.utils.MyDialogs;
import ru.aakumykov.me.sociocat.utils.MyUtils;

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
    @BindView(R.id.imageProgressBar) ProgressBar imageProgressBar;
    @BindView(R.id.imageView) ImageView imageView;
    @BindView(R.id.imagePlaceholder) ImageView imagePlaceholder;
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
    @BindView(R.id.timecodeSeekBar) SeekBar timecodeSeekBar;

    @BindView(R.id.tagsContainer) TagContainerLayout tagsContainer;
    @BindView(R.id.newTagInput) AutoCompleteTextView newTagInput;
    @BindView(R.id.addTagButton) Button addTagButton;

    @BindView(R.id.saveButton) Button saveButton;
    @BindView(R.id.cancelButton) Button cancelButton;

    private final static String TAG = "CardEdit_View";

    private InsertableYoutubePlayer insertableYoutubePlayer;
    private int seekbarPorgessValue = 0;

    private iCardEdit.Presenter presenter;
    private List<String> tagsList = new ArrayList<>();
    private boolean firstRun = true;
    private boolean exitIsExpected = false;
    private boolean selectImageMode = false;

    // Системные методы
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_edit_activity);
        ButterKnife.bind(this);

        setPageTitle(R.string.CARD_EDIT_page_title);
        activateUpButton();

        presenter = new CardEdit_Presenter();

        tagsContainer.setOnTagClickListener(this);

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

            case Constants.CODE_SELECT_IMAGE:
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

        if (firstRun) {
            firstRun = false;
            startEditWork(getIntent());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!exitIsExpected && !selectImageMode) {
            if (isFormFilled()) {
                try {
                    if (null != insertableYoutubePlayer)
                        insertableYoutubePlayer.pause();
                    presenter.saveEditState();
                } catch (Exception e) {
                    //showLongToast(R.string.CARD_EDIT_error_saving_edit_state);
                    showErrorMsg(R.string.CARD_EDIT_error_saving_edit_state, e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (null != insertableYoutubePlayer)
            insertableYoutubePlayer.pause();

        if (!selectImageMode) {
            try {
                presenter.restoreEditState();
            } catch (Exception e) {
                showErrorMsg(R.string.CARD_EDIT_error_restoring_edit_state, e.getMessage());
                e.printStackTrace();
            }
        }
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
    public void onUserLogin() {

    }

    @Override
    public void onUserLogout() {
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
//        super.onBackPressed(); // Если не закомментировать, выходит, игнорируя диалог
        onCancelClicked();
    }

    @Override
    public void showErrorMsg(int messageId, String consoleMessage) {
        super.showErrorMsg(messageId, consoleMessage);
        enableForm();
        MyUtils.hide(mediaThrobber);
        MyUtils.hide(imageProgressBar);
        scrollView.scrollTo(0,0);
    }


    // Интерфейсные методы
    @Override
    public void displayCard(Card card) {
        hideProgressMessage();

        String cardType = card.getType();

        switch (cardType) {
            case Constants.TEXT_CARD:
                displayQuote(card.getQuote(), card.getQuoteSource());
                break;
            case Constants.IMAGE_CARD:
                //displayImage(card.getImageURL());
                displayImageFromCard(card);
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

    @Override public void displayImageFromCard(Card card) {
        Uri localImageURI = card.getLocalImageURI();
        String remoteImageURL = card.getImageURL();

        if (null != localImageURI)
            displayImage(localImageURI.toString());
        else if (!TextUtils.isEmpty(remoteImageURL)) {
            displayImage(remoteImageURL);
        }
        else {
            removeImage();
        }
    }

    @Override
    public void displayImage(String imageURI) {

        if (TextUtils.isEmpty(imageURI)) {
            removeImage();
        }
        else {
            MyUtils.hide(imagePlaceholder);
            MyUtils.hide(imageHolder);
            MyUtils.show(mediaThrobber);

            try {
                Picasso.get().load(imageURI)
                        .into(imageView, new Callback() {
                            @Override
                            public void onSuccess() {
                                MyUtils.hide(mediaThrobber);
                                MyUtils.hide(imagePlaceholder);
                                MyUtils.show(imageHolder);
                                MyUtils.show(imageView);
                                MyUtils.show(discardImageButton);
                            }

                            @Override
                            public void onError(Exception e) {
                                showBrokenImage();
                                showErrorMsg(R.string.CARD_EDIT_error_displaying_image, e.getMessage());
                                e.printStackTrace();
                            }
                        });

            } catch (Exception e) {
                showBrokenImage();
                showErrorMsg(R.string.CARD_EDIT_error_displaying_image, e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @Override
    public void displayVideo(final String youtubeCode, @Nullable Double timecode) {

        if (TextUtils.isEmpty(youtubeCode)) {
            MyUtils.show(addMediaButton);
            return;
        }

        prepareForVideoCard();

        insertableYoutubePlayer.show(youtubeCode, 0.0f, InsertableYoutubePlayer.PlayerType.VIDEO_PLAYER, new InsertableYoutubePlayer.ShowCallbacks() {
            @Override
            public void onVideoShown() {
                MyUtils.show(convertToAudioButton);
                MyUtils.show(removeMediaButton);
                showTimecodeControls(timecode);
            }

            @Override
            public void onVideoShowError(String errorMsg) {
                showErrorMsg(R.string.CARD_EDIT_error_displaying_video, errorMsg);
            }
        });
    }

    @Override
    public void displayAudio(final String youtubeCode, @Nullable Double timecode) {

        if (null == youtubeCode) {
            MyUtils.show(addMediaButton);
            return;
        }

        prepareForAudioCard();

        insertableYoutubePlayer.show(youtubeCode, 0.0f, InsertableYoutubePlayer.PlayerType.AUDIO_PLAYER, new InsertableYoutubePlayer.ShowCallbacks() {
            @Override
            public void onVideoShown() {
                MyUtils.show(convertToVideoButton);
                MyUtils.show(removeMediaButton);
                showTimecodeControls(timecode);
            }

            @Override
            public void onVideoShowError(String errorMsg) {
                showErrorMsg(R.string.CARD_EDIT_error_adding_audio, errorMsg);
            }
        });
    }

    @Override
    public void removeImage() {
        imageView.setImageDrawable(null);

        MyUtils.hide(mediaThrobber);
        MyUtils.hide(imageView);
        MyUtils.hide(discardImageButton);

        MyUtils.show(imageHolder);
        MyUtils.show(imagePlaceholder);
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
    public Bitmap getImageBitmap() {
//        return ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        Drawable drawable = imageView.getDrawable();
        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
        Bitmap bitmap = bitmapDrawable.getBitmap();
        return bitmap;
    }

    @Override
    public String getDescription() {
        return descriptionInput.getText().toString();
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
        titleInput.setError(getResources().getString(msgId));
    }

    @Override
    public void showQuoteError(int msgId) {
        quoteInput.setError(getResources().getString(msgId));
    }

    @Override
    public void showImageError(int msgId) {
        showToast(msgId);
    }

    @Override
    public void showVideoError(int msgId) {
        showToast(msgId);
    }

    @Override
    public void showAudioError(int msgId) {
        showToast(msgId);
    }

    @Override public void showMediaError() {
        addMediaButtonContainer.setBackgroundResource(R.drawable.shape_red_border);
    }

    @Override public void hideMediaError() {
        addMediaButtonContainer.setBackground(null);
    }

    @Override
    public void showDescriptionError(int msgId) {
        descriptionInput.setError(getResources().getString(msgId));
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
    public void showImageProgressBar() {
        MyUtils.show(imageProgressBar);
    }

    @Override
    public void hideImageProgressBar() {
        MyUtils.hide(imageProgressBar);
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
    public void showCard(Card card) {
        exitIsExpected = true;
        Intent intent = new Intent(this, CardShow_View.class);
        intent.putExtra(Constants.CARD_KEY, card.getKey());
        startActivity(intent);
    }

    @Override
    public void addTag(String tag) {
        tagsContainer.addTag(tag);
        newTagInput.setText("");
    }

    @Override
    public void showDraftRestoreDialog(Card cardDraft) {

        MVPUtils.showDraftRestoreDialog(getSupportFragmentManager(), cardDraft, new DraftRestoreFragment.Callbacks() {
            @Override public void onDraftRestoreConfirmed() {
                displayCard(cardDraft);
            }

            @Override public void onDraftRestoreDeferred() {

            }

            @Override public void onDraftRestoreCanceled() {
                presenter.clearEditState();
            }
        });
    }


    // Методы событий интерсейса
    @OnClick(R.id.imagePlaceholder)
    public void onSelectImageClicked() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        selectImageMode = true;

        if (null != intent.resolveActivity(getPackageManager())) {
            startActivityForResult(
                    Intent.createChooser(intent, getResources().getString(R.string.select_image)),
                    Constants.CODE_SELECT_IMAGE
            );
        }
        else {
            showErrorMsg(R.string.CARD_EDIT_error_receiving_image,
                    "Error resolving activity for Intent.ACTION_GET_CONTENT");
        }
    }

    @OnClick(R.id.discardImageButton)
    void onResetImageClicked() {
        removeImage();
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
                            e.printStackTrace();
                        }
                    }
                });
    }

    @OnClick(R.id.removeMediaButton)
    void onRemoveMediaClicked() {
        presenter.removeMedia();
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

        try {
            if (RESULT_OK == resultCode)
                presenter.processSelectedImage(data);
        }
        catch (Exception e) {
            showErrorMsg(R.string.CARD_EDIT_error_processing_image, e.getMessage());
            e.printStackTrace();
        }
    }

    private void startEditWork(Intent intent) {
        try {
            presenter.processInputIntent(intent);
            presenter.loadTagsList(new iCardEdit.TagsListLoadCallbacks() {
                @Override
                public void onTagsListLoadSuccess(List<String> list) {
                    tagsList.addAll(list);
                    setTagAutocomplete();
                }

                @Override
                public void onTagsListLoadFail(String errorMsg) {
                    showErrorMsg(R.string.CARD_EDIT_error_loading_tags_list, errorMsg);
                }
            });

        }
        catch (Exception e) {
            showErrorMsg(R.string.CARD_EDIT_error_editing_card, e.getMessage());
            e.printStackTrace();
        }
    }

    private void setTagAutocomplete() {
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
        MyUtils.show(quoteSourceInput);
    }

    private void displayCommonCardParts(Card card) {
        titleInput.setText(card.getTitle());
        descriptionInput.setText(card.getDescription());
        tagsContainer.setTags(card.getTags());
    }

    private void changeFormSate(boolean isEnabled) {

        titleInput.setEnabled(isEnabled);
        quoteInput.setEnabled(isEnabled);
        quoteSourceInput.setEnabled(isEnabled);

        discardImageButton.setEnabled(isEnabled);

        addMediaButton.setEnabled(isEnabled);
        removeMediaButton.setEnabled(isEnabled);

        convertToVideoButton.setEnabled(isEnabled);
        convertToAudioButton.setEnabled(isEnabled);

        newTagInput.setEnabled(isEnabled);
        addTagButton.setEnabled(isEnabled);
        descriptionInput.setEnabled(isEnabled);

        saveButton.setEnabled(isEnabled);
    }

    private void showBrokenImage() {
        MyUtils.hide(mediaThrobber);
        Drawable drawable = getResources().getDrawable(R.drawable.ic_image_broken);
        imageView.setImageDrawable(drawable);
        MyUtils.show(imageHolder);
        MyUtils.show(imageView);
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
                int commaIndex = text.toString().indexOf(",");
                if (commaIndex > -1) {
                    String tag = text.substring(0, commaIndex);
                    presenter.processTag(tag);
                    String restText = text.substring(commaIndex+1, text.length());
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
            e.printStackTrace();
        }
    }

    private void prepareForVideoCard() {
        prepareMediaPlayer();
        removeMediaButton.setText(R.string.CARD_EDIT_remove_video);
    }

    private void prepareForAudioCard() {
        prepareMediaPlayer();
        removeMediaButton.setText(R.string.CARD_EDIT_remove_audio);
    }

    private void prepareMediaPlayer() {
        if (null != insertableYoutubePlayer)
            return;

        /*insertableYoutubePlayer = new MyYoutubePlayer(
                this,
                playerContainer,
                R.string.YOUTUBE_PLAYER_preparing_player,
                R.drawable.ic_player_play,
                R.drawable.ic_player_pause,
                R.drawable.ic_player_wait
        );*/

        insertableYoutubePlayer = new InsertableYoutubePlayer(
                this,
                playerContainer,
                R.string.CARD_EDIT_waiting_media_data
        );

        addMediaButton.setText(R.string.CARD_EDIT_add_youtube_link);
    }

    private void showTimecodeControls(Double timecode) {

        timecodeInput.setText(MyUtils.seconds2HHMMSS(timecode));

        int progressValue = 0;

        timecodeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    seekbarPorgessValue = progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                insertableYoutubePlayer.seekTo(seekbarPorgessValue, new InsertableYoutubePlayer.SeekCallbacks() {
                    @Override
                    public void onSeekComplete(float timeCode) {
                        timecodeInput.setText(String.valueOf(timeCode));
                    }
                });
            }
        });

        MyUtils.show(timecodeControlsContainer);
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
}
