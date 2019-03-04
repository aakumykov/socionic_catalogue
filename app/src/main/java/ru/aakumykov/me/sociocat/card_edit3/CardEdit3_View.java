package ru.aakumykov.me.sociocat.card_edit3;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
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
import android.widget.TextView;

import com.pierfrancescosoffritti.androidyoutubeplayer.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.YouTubePlayerView;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.listeners.YouTubePlayerInitListener;
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
import ru.aakumykov.me.sociocat.BaseView;
import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_edit.TagAutocompleteAdapter;
import ru.aakumykov.me.sociocat.card_show.CardShow_View;
import ru.aakumykov.me.sociocat.interfaces.iMyDialogs;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.utils.MyDialogs;
import ru.aakumykov.me.sociocat.utils.MyUtils;
import ru.aakumykov.me.sociocat.utils.MyYoutubePlayer;

//@RuntimePermissions
public class CardEdit3_View extends BaseView implements
        iCardEdit3.View,
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

    @BindView(R.id.videoMessage) TextView videoMessage;
    @BindView(R.id.videoPlayerHolder) FrameLayout videoPlayerHolder;
    @BindView(R.id.removeVideoButton) Button removeVideoButton;
    @BindView(R.id.addVideoButton) Button addVideoButton;

    @BindView(R.id.audioHolder) LinearLayout audioHolder;
    @BindView(R.id.addAudioButton) Button addAudioButton;

    @BindView(R.id.tagsContainer) TagContainerLayout tagsContainer;
    @BindView(R.id.newTagInput) AutoCompleteTextView newTagInput;
    @BindView(R.id.addTagButton) Button addTagButton;

    @BindView(R.id.saveButton) Button saveButton;
    @BindView(R.id.cancelButton) Button cancelButton;

    private final static String TAG = "CardEdit3_View";

    private YouTubePlayerView youTubePlayerView;
    private YouTubePlayer youTubePlayer;

    private MyYoutubePlayer audioPlayer;

    private iCardEdit3.Presenter presenter;
    private List<String> tagsList = new ArrayList<>();
    private boolean firstRun = true;
    private boolean exitIsExpected = false;


    // Системные методы
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_edit3_activity);
        ButterKnife.bind(this);

        setPageTitle(R.string.CARD_EDIT_page_title);
        activateUpButton();

        presenter = new CardEdit3_Presenter();

//        tagsList = new ArrayList<>();

        tagsContainer.setOnTagClickListener(this);
        //tagsContainer.setDragEnable(false);

        setTagWatcher();
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.linkView(this);

        if (firstRun) {
            firstRun = false;
            try {
                presenter.processInputIntent(getIntent());
                presenter.loadTagsList(new iCardEdit3.TagsListLoadCallbacks() {
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

            } catch (Exception e) {
                showErrorMsg(R.string.CARD_EDIT_error_editing_card, e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!exitIsExpected) {
            if (formIsFilled()) {
                try {
                    presenter.saveEditState();
                } catch (Exception e) {
                    showLongToast(R.string.CARD_EDIT_error_saving_edit_state);
                    showConsoleError(TAG, e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //presenter.restoreEditState();
    }

    @Override
    protected void onStop() {
        super.onStop();

//        if (exitIsExpected) presenter.clearEditState();
//        else presenter.saveEditState();

        presenter.unlinkView();
    }

    @Override
    public void onUserLogin() {

    }

    @Override
    public void onUserLogout() {

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
                cancelEdit();
                return true;
            case R.id.actionSave:
                saveCard();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed(); // Если не закомментировать, выходит, игнорируя диалог
        cancelEdit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        presenter.linkView(this); // обязательно!!!

        switch (requestCode) {
            case Constants.CODE_SELECT_IMAGE:
                try {
                    if (RESULT_OK == resultCode)
                        presenter.processIncomingImage(data);
                } catch (Exception e) {
                    showErrorMsg(R.string.CARD_EDIT_error_processing_image, e.getMessage());
                    e.printStackTrace();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
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
        hideProgressBar();

        switch (card.getType()) {
            case Constants.TEXT_CARD:
                displayQuote(card.getQuote(), card.getQuoteSource());
                break;
            case Constants.IMAGE_CARD:
                displayImage(card.getImageURL());
                break;
            case Constants.VIDEO_CARD:
                displayVideo(card.getVideoCode());
                break;
            case Constants.AUDIO_CARD:
                displayAudio(card.getAudioCode());
                break;
            default:
                showErrorMsg(R.string.wrong_card_type);
        }

        displayCommonCardParts(card);
        enableForm();
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
    public void displayVideo(final String videoCode) {

        if (TextUtils.isEmpty(videoCode)) {
            MyUtils.show(addVideoButton);
            return;
        }

        MyUtils.hide(addVideoButton);
        MyUtils.show(mediaThrobber);
        MyUtils.show(videoMessage);

        try {

            int playerWidth = MyUtils.getScreenWidth(this);
            int playerHeight = Math.round(MyUtils.getScreenWidth(this) * 9f / 16f);

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

                            MyUtils.hide(mediaThrobber);
                            MyUtils.hide(videoMessage);
                            MyUtils.show(videoPlayerHolder);
                            MyUtils.show(youTubePlayerView);
                            MyUtils.show(removeVideoButton);
                        }
                    });
                }
            }, true);

        } catch (Exception e) {
            showErrorMsg(R.string.CARD_EDIT_error_displaying_video, e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void displayAudio(final String audioCode) {
        if (null == audioCode) {
            MyUtils.show(addAudioButton);
            return;
        }

        audioPlayer = new MyYoutubePlayer(
                MyYoutubePlayer.PlayerType.AUDIO_PLAYER,
                R.string.YOUTUBE_PLAYER_preparing_player,
                this,
                audioHolder,
                null
                );

        audioPlayer.setVideo(audioCode, true);
    }

    @Override public void removeImage() {
        imageView.setImageDrawable(null);

        MyUtils.hide(mediaThrobber);
        MyUtils.hide(imageView);
        MyUtils.hide(discardImageButton);

        MyUtils.show(imageHolder);
        MyUtils.show(imagePlaceholder);
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
        return ((BitmapDrawable) imageView.getDrawable()).getBitmap();
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
    public void showDescriptionError(int msgId) {
        descriptionInput.setError(getResources().getString(msgId));
    }

    @Override
    public void disableForm() {
        MyUtils.disable(titleInput);
        MyUtils.disable(quoteInput);
        MyUtils.disable(quoteSourceInput);

        MyUtils.disable(discardImageButton);
        MyUtils.disable(addVideoButton);
        MyUtils.disable(removeVideoButton);

        MyUtils.disable(newTagInput);
        MyUtils.disable(addTagButton);
        MyUtils.disable(descriptionInput);

        MyUtils.disable(saveButton);

        tagsContainer.setOnTagClickListener(null);
    }

    @Override
    public void enableForm() {
        MyUtils.enable(titleInput);
        MyUtils.enable(quoteInput);
        MyUtils.enable(quoteSourceInput);

        MyUtils.enable(discardImageButton);
        MyUtils.enable(addVideoButton);
        MyUtils.enable(removeVideoButton);

        MyUtils.enable(newTagInput);
        MyUtils.enable(addTagButton);
        MyUtils.enable(descriptionInput);

        MyUtils.enable(saveButton);

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
    public boolean formIsFilled() {
        boolean changed = false;
        if (!TextUtils.isEmpty(getCardTitle())) changed = true;
        if (!TextUtils.isEmpty(getQuote())) changed = true;
        if (!TextUtils.isEmpty(getQuoteSource())) changed = true;
        if (!TextUtils.isEmpty(getDescription())) changed = true;
        if (tagsContainer.getTags().size() > 0) changed = true;
        if (View.VISIBLE == videoPlayerHolder.getVisibility()) changed = true;

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


    // Методы событий интерсейса
    @OnClick(R.id.imagePlaceholder)
    public void selectImage() {
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
            showErrorMsg(R.string.CARD_EDIT_error_receiving_image,
                    "Error resolving activity for Intent.ACTION_GET_CONTENT");
        }
    }

    @OnClick(R.id.discardImageButton)
    void resetImageClicked() {
        removeImage();
    }

    @OnClick(R.id.addVideoButton)
    void addVideoClicked() {
        // TODO: переименовать в "inputStringDialog"
        MyDialogs.addYoutubeVideoDialog(this, new iMyDialogs.StringInputCallback() {
            @Override
            public void onDialogWithStringYes(String text) {
                presenter.processVideoLink(text);
            }
        });
    }

    @OnClick(R.id.removeVideoButton)
    void removeVideoClicked() {
        if (null != youTubePlayer)
            youTubePlayer.pause();
        youTubePlayerView.release();

        MyUtils.hide(videoPlayerHolder);
        MyUtils.hide(removeVideoButton);
        MyUtils.show(addVideoButton);
    }

    // Бредятина в логике.
    @OnClick(R.id.addAudioButton)
    void addAudioClicked() {
        // TODO: переименовать в "inputStringDialog"
        MyDialogs.addYoutubeVideoDialog(this, new iMyDialogs.StringInputCallback() {
            @Override public void onDialogWithStringYes(String text) {
                presenter.processAudioLink(text);
            }
        });
    }

    @OnClick(R.id.addTagButton)
    void addTagClicked() {
        presenter.processTag(newTagInput.getText().toString());
    }

    @OnClick(R.id.saveButton)
    void saveCard() {
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
    void cancelEdit() {
        if (formIsFilled()) {
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
                            clearSharedPrefs(getSharedPrefs(Constants.SHARED_PREFERENCES_CARD_EDIT), Constants.CARD);
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
    public void onTagCrossClick(int position) {
        tagsContainer.removeTag(position);
    }

    @Override
    public void onSelectedTagDrag(int position, String text) {

    }


    // Внутренние методы
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
        tagsContainer.setTags(new ArrayList<String>(card.getTags().keySet()));
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
        setResult(RESULT_CANCELED);
        finish();
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
            presenter.saveCard(true);
        } catch (Exception e) {
            showErrorMsg(R.string.CARD_EDIT_error_saving_card, e.getMessage());
            e.printStackTrace();
        }
    }

}
