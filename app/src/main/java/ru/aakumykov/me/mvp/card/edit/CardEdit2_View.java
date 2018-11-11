package ru.aakumykov.me.mvp.card.edit;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.lujun.androidtagview.TagContainerLayout;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;
import ru.aakumykov.me.mvp.BaseView;
import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.card.CardEdit2_Presenter;
import ru.aakumykov.me.mvp.card.iCardEdit2;
import ru.aakumykov.me.mvp.card_show.CardShow_View;
import ru.aakumykov.me.mvp.models.Card;
import ru.aakumykov.me.mvp.utils.MyUtils;

@RuntimePermissions
public class CardEdit2_View extends BaseView implements
    iCardEdit2.View
{
    @BindView(R.id.titleView) EditText titleView;

    @BindView(R.id.modeSwitcher) LinearLayout modeSwitcher;
    @BindView(R.id.modeLabel) TextView modeLabel;
    @BindView(R.id.textModeSwitch) ImageView textModeSwitch;
    @BindView(R.id.imageModeSwitch) ImageView imageModeSwitch;
    @BindView(R.id.audioModeSwitch) ImageView audioModeSwitch;
    @BindView(R.id.videoModeSwitch) ImageView videoModeSwitch;

    @BindView(R.id.mediaHolder) FrameLayout mediaHolder;

    @BindView(R.id.quoteView) EditText quoteView;

    @BindView(R.id.imageHolder) ConstraintLayout imageHolder;
    @BindView(R.id.imageView) ImageView imageView;
    @BindView(R.id.imagePlaceholder) ImageView imagePlaceholder;
    @BindView(R.id.discardImageButton) ImageView discardImageButton;
    @BindView(R.id.imageProgressBar) ProgressBar imageProgressBar;

    @BindView(R.id.descriptionView) EditText descriptionView;

    @BindView(R.id.tagsContainer) TagContainerLayout tagsContainer;
    @BindView(R.id.newTagInput) EditText newTagInput;
    @BindView(R.id.addTagButton) Button addTagButton;

    @BindView(R.id.saveButton) Button saveButton;
    @BindView(R.id.cancelButton) Button cancelButton;


    private final static String TAG = "CardEdit2_View";
    private iCardEdit2.Presenter presenter;
    private boolean firstRun = true;


    // Системные методы
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_edit_activity);
        ButterKnife.bind(this);

        activateUpButton();

        CardEdit2_ViewPermissionsDispatcher.checkPermissionsWithPermissionCheck(this);

        presenter = new CardEdit2_Presenter();
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.linkView(this);

        if (firstRun) {
            firstRun = false;
            presenter.makeStartDecision(getIntent());
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
        if (!userLoggedIn()) closePage();
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
//            SelectedFile selectedFile = new SelectedFile();
//            selectedFile.setDataURI();
            try {
                presenter.processInputData(Constants.MODE_SELECT, data);
            } catch (Exception e) {
                showErrorMsg(R.string.CARD_EDIT_error_processing_data, e.getMessage());
            }
        }
        else if (RESULT_CANCELED == resultCode) {
            Log.d(TAG, "Media selection cancelled");
        }
        else {
            showErrorMsg(R.string.CARD_EDIT_unknown_error);
            Log.e(TAG, "Unknown result code ("+resultCode+")");
        }
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
            default:
                showErrorMsg(R.string.CARD_EDIT_error_editing_card);
                Log.e(TAG, "Unknown card type '"+card.getType()+"'");
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
    public void displayQuote(String text) {
        switchTextMode();
        quoteView.setText(text);
    }

    @Override
    public void displayImage(final Uri imageURI) {

        switchImageMode();

        Picasso.get().load(imageURI)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
//                        storeImageURI(imageURI);
                        MyUtils.hide(imageProgressBar);
                        MyUtils.hide(imagePlaceholder);
                        MyUtils.show(imageView);
                        MyUtils.show(discardImageButton);
                    }

                    @Override
                    public void onError(Exception e) {
//                        clearImageURI();
                        MyUtils.hide(imageProgressBar);
                        showBrokenImage();
                        showErrorMsg(R.string.error_loading_image);
                        e.printStackTrace();
                    }
                });
    }

    @Override
    public void showBrokenImage() {
        imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_image_broken));
        MyUtils.show(imageView);
        MyUtils.show(imageHolder);
//        MyUtils.hide(discardImageButton);
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
    public String getCardDescription() {
        return descriptionView.getText().toString();
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
        saveButton.setEnabled(false);

        MyUtils.show(imageProgressBar);
    }

    @Override
    public void enableForm() {
        titleView.setEnabled(true);
        quoteView.setEnabled(true);
        descriptionView.setEnabled(true);
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
    public void finishEdit(Card card) {
        Intent intent = new Intent();
        intent.putExtra(Constants.CARD, card);
        setResult(RESULT_OK, intent);
        finish();
    }

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

        quoteView.requestFocus();

        presenter.setCardType(Constants.TEXT_CARD);
    }

    @OnClick(R.id.imageModeSwitch)
    void switchImageMode() {
        hideModeSwitcher();
        MyUtils.show(mediaHolder);
        MyUtils.show(imageHolder);
        MyUtils.show(imagePlaceholder);
        presenter.setCardType(Constants.IMAGE_CARD);
    }

    @OnClick(R.id.audioModeSwitch)
    void switchAudioMode() {

    }

    @OnClick(R.id.videoModeSwitch)
    void switchVideoMode() {

    }

    @OnClick(R.id.saveButton)
    void save() {
        try {
            presenter.saveCard();
        } catch (Exception e) {
            enableForm();
            showErrorMsg(R.string.CARD_EDIT_error_saving_card, e.getMessage());
            e.printStackTrace();
        }
    }

    @OnClick(R.id.cancelButton)
    void cancel() {
        setResult(RESULT_CANCELED);
        finish();
    }

    @OnClick(R.id.discardImageButton)
    void removeImage() {
        MyUtils.hide(imageView);
        MyUtils.hide(discardImageButton);
        MyUtils.show(imagePlaceholder);

//        clearImageURI();
        presenter.forgetSelectedFile();
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

    }



    // Внутренние методы
    private void displayTextCard(Card card) {
        switchTextMode();
        displayCommonCardParts(card);
        quoteView.setText(card.getQuote());
    }

    private void displayImageCard(Card card) {
        switchImageMode();
        displayCommonCardParts(card);
        displayImage(card.getImageURL());
    }

    private void displayCommonCardParts(Card card) {
        titleView.setText(card.getTitle());
        descriptionView.setText(card.getDescription());
        showTags(card.getTags());
    }

    private void displayImage(String imageURI) {
        try {
            Uri uri = Uri.parse(imageURI);
            displayImage(uri);
        } catch (Exception e) {
            showErrorMsg(R.string.error_loading_image);
            showBrokenImage();
        }
    }

    private void showTags(HashMap<String,Boolean> tagsMap) {
        Log.d(TAG, "showTags(), "+tagsMap);
        if (null != tagsMap) {
            List<String> tagsList = new ArrayList<>(tagsMap.keySet());
            tagsContainer.setTags(tagsList);
            tagsContainer.setEnableCross(true);
        }
    }

//    private void storeImageURI(Uri uri) {
//        imageView.setTag(R.id.imageURI_tag, uri);
//    }
//    private void clearImageURI() {
//        imageView.setTag(R.id.imageURI_tag, null);
//    }


    // Другие
    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    void checkPermissions() {

    }
}
