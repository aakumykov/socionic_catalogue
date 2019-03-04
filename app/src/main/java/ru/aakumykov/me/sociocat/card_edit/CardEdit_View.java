package ru.aakumykov.me.sociocat.card_edit;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
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
import android.widget.TextView;

import com.pierfrancescosoffritti.androidyoutubeplayer.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.YouTubePlayerView;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.listeners.YouTubePlayerInitListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;
import ru.aakumykov.me.sociocat.BaseView;
import ru.aakumykov.me.sociocat.Config;
import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_show.CardShow_View;
import ru.aakumykov.me.sociocat.interfaces.iMyDialogs;
import ru.aakumykov.me.sociocat.login.Login_View;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.utils.MVPUtils.FileInfo;
import ru.aakumykov.me.sociocat.utils.MVPUtils.MVPUtils;
import ru.aakumykov.me.sociocat.utils.MVPUtils.iMVPUtils;
import ru.aakumykov.me.sociocat.utils.MyDialogs;
import ru.aakumykov.me.sociocat.utils.MyUtils;

@RuntimePermissions
public class CardEdit_View extends BaseView implements
    iCardEdit.View,
    TagView.OnTagClickListener
{
    @BindView(R.id.titleView) EditText titleView;
    @BindView(R.id.contentWaitProgressBar) ProgressBar contentWaitProgressBar;

    @BindView(R.id.modeSwitcher) LinearLayout modeSwitcher;
    @BindView(R.id.modeLabel) TextView modeLabel;
    @BindView(R.id.textModeSwitch) ImageView textModeSwitch;
    @BindView(R.id.imageModeSwitch) ImageView imageModeSwitch;
    @BindView(R.id.audioModeSwitch) ImageView audioModeSwitch;
    @BindView(R.id.videoModeSwitch) ImageView videoModeSwitch;

    @BindView(R.id.mediaHolder) LinearLayout mediaHolder;

    @BindView(R.id.quoteView) EditText quoteView;
    @BindView(R.id.quoteSourceView) EditText quoteSourceView;

    @BindView(R.id.videoPlayerHolder) FrameLayout videoPlayerHolder;
    @BindView(R.id.addVideoButton) Button addVideoButton;
    @BindView(R.id.removeVideoButton) Button removeVideoButton;
    @BindView(R.id.videoCodeView) TextView videoCodeView;

    @BindView(R.id.imageHolder) ConstraintLayout imageHolder;
    @BindView(R.id.imageView) ImageView imageView;
    @BindView(R.id.imagePlaceholder) ImageView imagePlaceholder;
    @BindView(R.id.discardImageButton) ImageView discardImageButton;
    @BindView(R.id.imageProgressBar) ProgressBar imageProgressBar;

    @BindView(R.id.descriptionView) EditText descriptionView;

    @BindView(R.id.tagsContainer) TagContainerLayout tagsContainer;
    @BindView(R.id.newTagInput) AutoCompleteTextView newTagInput;
    @BindView(R.id.addTagButton) Button addTagButton;

    @BindView(R.id.saveButton) Button saveButton;
    @BindView(R.id.cancelButton) Button cancelButton;

    private YouTubePlayerView youTubePlayerView;
    private YouTubePlayer youTubePlayer;

    private final static String TAG = "CardEdit_View";
    private iCardEdit.Presenter presenter;
    private boolean firstRun = true;

    private List<String> tagsList = new ArrayList<>();
    private TagAutocompleteAdapter tagAutocompleteAdapter;


    // Системные методы
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_edit_activity);
        ButterKnife.bind(this);

        activateUpButton();

        CardEdit_ViewPermissionsDispatcher.checkPermissionsWithPermissionCheck(this);

        tagsContainer.setOnTagClickListener(this);

        presenter = new CardEdit_Presenter();

        setTagWatcher();
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.linkView(this);

        if (firstRun) {
            firstRun = false;
            presenter.beginWork(getIntent());
            presenter.loadTagsList(new iCardEdit.TagsListCallbacks() {
                @Override
                public void onTagsListSuccess(List<String> list) {
                    tagsList.addAll(list);
                    setTagAutocomplete();
                }

                @Override
                public void onTagsListFail(String errorMsg) {
                    showErrorMsg(R.string.CARD_EDIT_error_loading_tags_list, errorMsg);
                }
            });
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.unlinkView();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onUserLogin() {

    }

    @Override
    public void onUserLogout() {
        if (!auth().isUserLoggedIn()) closePage();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                cancel();
                break;

            case R.id.actionSave:
                save();
                break;

            default:
                super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        presenter.linkView(this);

        if (RESULT_OK == resultCode) {

            switch (requestCode) {
                case Constants.CODE_SELECT_IMAGE:
                    try {
                        presenter.processIncomingImage(data);
                    } catch (Exception e) {
                        showErrorMsg(R.string.CARD_EDIT_error_processing_image, e.getMessage());
                        e.printStackTrace();
                    }
                    break;

                default:
                    break;
            }

        }
    }

    @Override public void onBackPressed() {
        super.onBackPressed();
        cancel();
    }

    // Интерфейсные методы
    @Override
    public void displayCard(Card card) {

        if (null == card) {
            showErrorMsg(R.string.CARD_EDIT_error_displaying_card);
            Log.e(TAG, "Card is null");
            return;
        }

        switch (card.getType()) {
            case Constants.TEXT_CARD:
                displayTextCard(card);
                break;
            case Constants.IMAGE_CARD:
                displayImageCard(card);
                break;
            case Constants.VIDEO_CARD:
                displayVideoCard(card);
                break;
            default:
                showErrorMsg(R.string.CARD_EDIT_error_editing_card);
                Log.e(TAG, "Unknown card_edit type '"+card.getType()+"'");
        }
    }

    @Override
    public void showModeSwitcher() {
        MyUtils.show(modeLabel);
        MyUtils.show(modeSwitcher);
    }

    @Override
    public void hideModeSwitcher() {
        MyUtils.hide(modeLabel);
        MyUtils.hide(modeSwitcher);
    }

    @Override
    public void displayTitle(String text) {
        titleView.setText(text);
    }

    @Override
    public void displayQuote(String text) {
        switchTextMode();

        quoteView.setText( text.trim() );
    }

    @Override
    public void displayImage(String imageURI, boolean unprocessedYet) {

        hideModeSwitcher();

        showImageProgressBar();

        try {
            Uri uri = Uri.parse(imageURI);

            MVPUtils.loadImageWithResizeInto(
                    uri,
                    imageView,
                    unprocessedYet,
                    Config.MAX_CARD_IMAGE_WIDTH,
                    Config.MAX_CARD_IMAGE_HEIGHT,
                    new iMVPUtils.ImageLoadWithResizeCallbacks() {
                        @Override
                        public void onImageLoadWithResizeSuccess(FileInfo fileInfo) {
                            hideImageProgressBar();

                            MyUtils.hide(imagePlaceholder);

                            MyUtils.show(mediaHolder);
                            MyUtils.show(imageHolder);
                            MyUtils.show(imageView);
                            MyUtils.show(discardImageButton);
                        }

                        @Override
                        public void onImageLoadWithResizeFail(String errorMsg) {
                            hideImageProgressBar();
                            showBrokenImage();
                        }
                    }
            );

        }catch (Exception e) {
            hideImageProgressBar();
            showBrokenImage();
            e.printStackTrace();
        }
    }

    @Override
    public void displayImageBitmap(Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);

        hideModeSwitcher();
        MyUtils.hide(imagePlaceholder);
        MyUtils.hide(imageProgressBar);

        MyUtils.show(mediaHolder);
        MyUtils.show(imageHolder);
        MyUtils.show(imageView);
        MyUtils.show(discardImageButton);
    }

    @Override
    public void displayVideo(final String videoCode) {
        prepareVideoMode();

        MyUtils.hide(addVideoButton);
        MyUtils.show(removeVideoButton);

        int playerWidth = MyUtils.getScreenWidth(this);
        int playerHeight = Math.round(MyUtils.getScreenWidth(this) * 9/16);

        youTubePlayerView = new YouTubePlayerView(this);
        youTubePlayerView.setMinimumWidth(playerWidth);
        youTubePlayerView.setMinimumHeight(playerHeight);

        videoPlayerHolder.addView(youTubePlayerView);

        youTubePlayerView.initialize(new YouTubePlayerInitListener() {
            @Override
            public void onInitSuccess(@NonNull final YouTubePlayer initializedYouTubePlayer) {
                initializedYouTubePlayer.addListener(new AbstractYouTubePlayerListener() {
                    @Override
                    public void onReady() {
                        youTubePlayer = initializedYouTubePlayer;
                        youTubePlayer.cueVideo(videoCode, 0.0f);
                        MyUtils.show(youTubePlayerView);
                    }
                });
            }
        }, true);
    }

    @Override
    public void showBrokenImage() {
        MyUtils.hide(imageView);
        imagePlaceholder.setImageDrawable(getResources().getDrawable(R.drawable.ic_image_broken));
        MyUtils.show(imagePlaceholder);
    }

    @Override
    public String getCardTitle() {
        return titleView.getText().toString();
    }

    @Override
    public String getCardQuote() {
        return quoteView.getText().toString();
    }

    @Override
    public String getCardQuoteSource() {
        return quoteSourceView.getText().toString();
    }

    @Override
    public String getCardDescription() {
        return descriptionView.getText().toString();
    }

    @Override
    public HashMap<String,Boolean> getCardTags() {
        HashMap<String,Boolean> map = new HashMap<>();
        List<String> tagsList = tagsContainer.getTags();
        for (String tagName : tagsList) {
            map.put(tagName, true);
        }
        return map;
    }

    @Override
    public Bitmap getImageBitmap() {
        return ((BitmapDrawable) imageView.getDrawable()).getBitmap();

    }

    @Override
    public void addTag(String tag) {
        tagsContainer.addTag(tag);
    }

    @Override
    public void storeCardVideoCode(String videoCode) {
        videoCodeView.setText(videoCode);
    }

    @Override
    public String getCardVideoCode() {
        return videoCodeView.getText().toString();
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
    public void setImageUploadProgress(int progress) {
//        imageProgressBar.setProgress(progress);
        Log.d(TAG, "imageUploadProgress: "+progress);
    }

    @Override
    public void disableForm() {
        titleView.setEnabled(false);
        quoteView.setEnabled(false);
        descriptionView.setEnabled(false);

        newTagInput.setEnabled(false);
        addTagButton.setEnabled(false);

        saveButton.setEnabled(false);

        MyUtils.show(imageProgressBar);
    }

    @Override
    public void enableForm() {
        titleView.setEnabled(true);
        quoteView.setEnabled(true);
        descriptionView.setEnabled(true);

        newTagInput.setEnabled(false);
        addTagButton.setEnabled(false);

        saveButton.setEnabled(true);

        MyUtils.hide(imageProgressBar);
    }

    @Override
    public void goCardShow(Card card) {
        Intent intent = new Intent(this, CardShow_View.class);
        intent.putExtra(Constants.CARD_KEY, card.getKey());
        startActivity(intent);
    }

    @Override
    public void finishEdit(Card card, boolean showAfterFinish) {

        Intent intent = new Intent();

        if (showAfterFinish) {
            intent.setClass(this, CardShow_View.class);
            intent.putExtra(Constants.CARD_KEY, card.getKey());
            startActivity(intent);
        } else {
            intent.putExtra(Constants.CARD, card);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    // TODO: перенести в Utils
    @Override
    public String detectMimeType(Uri dataURI) {
        ContentResolver cr = this.getContentResolver();
        return cr.getType(dataURI);
    }


    // Методы нажатий
    @OnClick(R.id.textModeSwitch)
    void switchTextMode() {
        hideModeSwitcher();

        MyUtils.show(mediaHolder);
        MyUtils.show(quoteView);
        MyUtils.show(quoteSourceView);

        quoteView.requestFocus();

        presenter.setCardType(Constants.TEXT_CARD);
    }

    @OnClick(R.id.imageModeSwitch)
    void onImageModeSwitch() {
        switchImageMode(true);
    }

    @OnClick(R.id.audioModeSwitch)
    void switchAudioMode() {
        showToast(R.string.INFO_not_implemented_yet);
    }

    @OnClick(R.id.videoModeSwitch)
    void switchVideoMode() {
        prepareVideoMode();
        addVideo();
    }

    @OnClick(R.id.addVideoButton)
    void addVideo() {
        MyDialogs.addYoutubeVideoDialog(this, new iMyDialogs.StringInputCallback() {
            @Override
            public String onYesClicked(String text) {
                String videoCode = MVPUtils.extractYoutubeVideoCode(text);
                storeCardVideoCode(videoCode);
                displayVideo(videoCode);
                return null;
            }
            @Override
            public void onSuccess() {

            }
        });
    }

    @OnClick(R.id.removeVideoButton)
    void removeVideo() {
        // youTubePlayer может ещё быть не инициализированным
        if (null != youTubePlayer) youTubePlayer.pause();

        youTubePlayerView.release();

        videoPlayerHolder.removeAllViews();

        videoCodeView.setText("");

        MyUtils.show(addVideoButton);
        MyUtils.hide(removeVideoButton);
    }

    @OnClick(R.id.saveButton)
    void save() {

        final String tag = newTagInput.getText().toString();

        if (!TextUtils.isEmpty(tag)) {
            MyDialogs.forgottenTagDialog(
                    this,
                    getString(R.string.CARD_EDIT_forgotten_tag_dialog_message, tag),
                    new iMyDialogs.StandardCallbacks() {
                        @Override
                        public void onCancelInDialog() {

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
                            presenter.processTagInput(tag, new iCardEdit.TagProcessCallbacks() {
                                @Override
                                public void onTagProcessed() {
                                    saveCardReal();
                                }
                            });
                        }
                    });
        } else {
            saveCardReal();
        }
    }

    @OnClick(R.id.cancelButton)
    void cancel() {
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
                        setResult(RESULT_CANCELED);
                        finish();
                    }
                }
        );
    }

    @OnClick(R.id.discardImageButton)
    void removeImage() {
        MyUtils.hide(imageView);
        MyUtils.hide(discardImageButton);

        imagePlaceholder.setImageDrawable(getResources().getDrawable(R.drawable.ic_image_placeholder));
        MyUtils.show(imagePlaceholder);

//        imageView.setTag(Constants.)
    }

    @OnClick(R.id.imagePlaceholder)
    public void selectImage() {
        Log.d(TAG, "selectImage()");

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        if (null != intent.resolveActivity(getPackageManager())) {
            startActivityForResult(
                    Intent.createChooser(intent, getResources().getString(R.string.select_image)),
                    Constants.CODE_SELECT_IMAGE
            );
        }
        else {
            showErrorMsg(R.string.CARD_EDIT_error_receiving_image);
            Log.e(TAG, "Error resolving activity for Intent.ACTION_GET_CONTENT");
        }
    }

    @OnClick(R.id.addTagButton)
    public void addTag() {
        String text = newTagInput.getText().toString();
        presenter.processTagInput(text);
        newTagInput.setText("");
        newTagInput.requestFocus();
    }


    // Методы нажатий меток
    @Override
    public void onTagClick(int position, String text) {

    }

    @Override
    public void onTagLongClick(int position, String text) {

    }

    @Override
    public void onTagCrossClick(int position) {
        tagsContainer.removeTag(position);
    }


    // Внутренние методы
    private void switchImageMode(boolean startSelectingImage) {
        hideModeSwitcher();

        MyUtils.show(mediaHolder);
        MyUtils.show(imageHolder);
        MyUtils.show(imagePlaceholder);

        presenter.setCardType(Constants.IMAGE_CARD);

        if (startSelectingImage) selectImage();
    }

    private void prepareVideoMode() {
        hideModeSwitcher();
        MyUtils.show(mediaHolder);
        MyUtils.show(addVideoButton);
        presenter.setCardType(Constants.VIDEO_CARD); // TODO: дубликат установки типа карточки
    }

    private void displayTextCard(Card card) {
        switchTextMode();
        displayCommonCardParts(card);
        quoteView.setText(card.getQuote());
        quoteSourceView.setText(card.getQuoteSource());
    }

    private void displayImageCard(Card card) {
        switchImageMode(false);
        displayCommonCardParts(card);
        displayImage(card.getImageURL(), false);
    }

    private void displayVideoCard(Card card) {
        displayCommonCardParts(card);
        displayVideo(card.getVideoCode());
        storeCardVideoCode(card.getVideoCode());
    }

    private void displayCommonCardParts(Card card) {
        titleView.setText(card.getTitle());
        descriptionView.setText(card.getDescription());
        showTags(card.getTags());
    }

    private void showTags(HashMap<String,Boolean> tagsMap) {
        Log.d(TAG, "displayTags(), "+tagsMap);
        if (null != tagsMap) {
            List<String> tagsList = new ArrayList<>(tagsMap.keySet());
            tagsContainer.setTags(tagsList);
            tagsContainer.setEnableCross(true);
        }
    }

    private void setTagWatcher() {

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
                    presenter.processTagInput(tag);
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

    private void setTagAutocomplete() {
        newTagInput.setThreshold(1);

        tagAutocompleteAdapter = new TagAutocompleteAdapter(
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
//                newTagInput.requestFocus();
            }
        });
    }

    private void saveCardReal() {
        // TODO: показывать причину ошибки сохранения...
        try {
            presenter.saveCard();
        } catch (Exception e) {
            enableForm();
            hideProgressBar();
            showErrorMsg(R.string.CARD_EDIT_error_saving_card, e.getMessage());
            e.printStackTrace();
        }
    }


    // Другие
    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    void checkPermissions() {

    }
}
