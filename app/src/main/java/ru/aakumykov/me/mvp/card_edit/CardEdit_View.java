package ru.aakumykov.me.mvp.card_edit;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;
import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.MyUtils;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.card_view.CardView_View;
import ru.aakumykov.me.mvp.models.Card;

// TODO: кнопка Вверх как Отмена

@RuntimePermissions
public class CardEdit_View extends AppCompatActivity
        implements iCardEdit.View, View.OnClickListener {

    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.messageView) TextView messageView;
    @BindView(R.id.titleView) EditText titleView;
    @BindView(R.id.quoteView) EditText quoteView;

    @BindView(R.id.imageHolder) ConstraintLayout imageHolder;
    @BindView(R.id.localImageView) ImageView localImageView;
    @BindView(R.id.remoteImageView) ImageView remoteImageView;
    @BindView(R.id.discardImageButton) ImageView discardImageButton;
    @BindView(R.id.imageProgressBar) ProgressBar imageProgressBar;

    @BindView(R.id.descriptionView) EditText descriptionView;
    @BindView(R.id.saveButton) Button saveButton;
    @BindView(R.id.cancelButton) Button cancelButton;


    private final static String TAG = "CardEdit_View";
    private iCardEdit.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG+"_L-CYCLE", "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_edit_activity);
        ButterKnife.bind(this);

        this.presenter = new CardEdit_Presenter();
        this.presenter.linkView(this);

        saveButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        discardImageButton.setOnClickListener(this);
        localImageView.setOnClickListener(this);

        CardEdit_ViewPermissionsDispatcher.checkPermissionsWithPermissionCheck(this);

        processInputIntent();
    }

    @Override
    protected void onStart() {
        Log.d(TAG+"_L-CYCLE", "onStart()");
        super.onStart();
        this.presenter.linkView(this);
    }

    @Override
    protected void onStop() {
        Log.d(TAG+"_L-CYCLE", "onStop()");
        super.onStop();
        presenter.unlinkView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.saveButton:
                presenter.onSaveButtonClicked();
                break;
            case R.id.cancelButton:
                presenter.onCancelButtonClicked();
                break;
            case R.id.localImageView:
                presenter.onSelectImageClicked();
                break;
            case R.id.discardImageButton:
                presenter.onImageDiscardClicked();
                break;
            default:
                Log.e(TAG, "Clicked element with unknown id: "+v.getId());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.card_edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionSave:
                presenter.onSaveButtonClicked();
                break;
            default:
                super.onOptionsItemSelected(item);
                break;
        }
        return true;
    }


    @Override
    public void displayTextCard(Card card) {
        Log.d(TAG, "displayTextCard()");
        displayCommonCardParts(card);
        quoteView.setText(card.getQuote());
        MyUtils.show(quoteView);
    }

    @Override
    public void displayImageCard(Card card) {
        Log.d(TAG, "displayImageCard()");
        displayCommonCardParts(card);
        displayRemoteImage(card.getImageURL());
    }


    @Override
    public void displayRemoteImage(String imageURI) {
        if (null == imageURI) {
            showError(R.string.error_loading_image);
            MyUtils.show(imageHolder);
            MyUtils.show(localImageView);
            MyUtils.hide(imageProgressBar);
            MyUtils.hide(discardImageButton);
            return;
        }

        Uri uri = Uri.parse(imageURI);
        if (null == uri) {
            showError(R.string.error_loading_image);
            Log.e(TAG, "Wrong image URI: "+imageURI);
            return;
        }

        displayRemoteImage(uri);
    }

    @Override
    public void displayRemoteImage(Uri imageURI) {
        hideMessage();
        MyUtils.hide(localImageView);
        MyUtils.show(imageHolder);
        displayImage(remoteImageView, imageURI);
    }

    @Override
    public void displayLocalImage(String imageURI) {
        Uri uri = Uri.parse(imageURI);

        if (null != uri) {
            displayLocalImage(uri);
        } else {
            showError(R.string.error_loading_image);
            Log.e(TAG, "Wrong image URI: "+imageURI);
        }
    }

    @Override
    public void displayLocalImage(Uri imageURI) {
        hideMessage();
        MyUtils.hide(remoteImageView);
        MyUtils.show(imageHolder);
        MyUtils.show(discardImageButton);
        displayImage(localImageView, imageURI);
    }

    private void displayImage(final ImageView imageView, Uri imageURI) {

        MyUtils.show(imageProgressBar);

        Picasso.get().load(imageURI).into(imageView, new Callback() {
            @Override
            public void onSuccess() {
                MyUtils.hide(imageProgressBar);
                MyUtils.show(discardImageButton);
                MyUtils.show(imageView);
            }

            @Override
            public void onError(Exception e) {
                showError(R.string.error_loading_image);
                displayBrokenImage();
            }
        });
    }


    @Override
    public void displayBrokenImage() {
        Log.d(TAG, "displayBrokenImage()");
        localImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_image_broken));
        MyUtils.show(imageHolder);
        MyUtils.show(localImageView);
        MyUtils.hide(discardImageButton);
        MyUtils.hide(imageProgressBar);
    }


    @Override
    public void removeImage() {
        Log.d(TAG, "removeImage()");

        remoteImageView.setImageDrawable(null);
        MyUtils.hide(remoteImageView);

        localImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_image_placeholder));
        MyUtils.show(localImageView);

        MyUtils.hide(discardImageButton);
//        localImageView.setOnClickListener(this);
    }


    // TODO: запрос разрешений
    // TODO: не показывать ошибку при ручном отказе
    @Override
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG+"_L-CYCLE", "onActivityResult(requestCode: "+requestCode+", resultCode: "+resultCode+", ...)");

        presenter.linkView(this);

        switch (requestCode) {
            case Constants.CODE_SELECT_IMAGE:
                processSelectedImage(resultCode, data);
                break;
            default:
                break;
        }
    }

    private void processSelectedImage(int resultCode, @Nullable Intent data) {
        Log.d(TAG, "processSelectedImage()");

        if (RESULT_CANCELED == resultCode)
            return;

        if (RESULT_OK != resultCode) {
            showError(R.string.error_selecting_image);
            return;
        }

        if (null == data) {
            showError(R.string.image_data_error);
            return;
        }

        Uri dataURI = data.getData();

        if (null == dataURI) {
            showError(R.string.image_data_error);
            return;
        }

        String mimeType =  this.getContentResolver().getType(dataURI);

        presenter.onImageSelected(dataURI, mimeType);
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
    public void finishEdit(Card card) {
        Log.d(TAG, "finishEdit(), "+card);
        Intent intent = new Intent();
        int resultCode = (null != card) ? RESULT_OK : RESULT_CANCELED;
        setResult(resultCode, intent);
        intent.putExtra(Constants.CARD, card);
        finish();
    }

    @Override
    public void displayNewCard(Card card) {
        Log.d(TAG, "displayNewCard(), "+card);
        Intent intent = new Intent();
        intent.setClass(this, CardView_View.class);
        intent.putExtra(Constants.CARD_KEY, card.getKey());
        startActivity(intent);
    }

    @Override
    public void prepareTextCard() {
        MyUtils.show(quoteView);
    }

    @Override
    public void prepareImageCard() {
        MyUtils.show(imageHolder);
        MyUtils.show(localImageView);
        MyUtils.hide(imageProgressBar);
        MyUtils.hide(discardImageButton);
    }

    @Override
    public void showProgressBar() {
        MyUtils.show(progressBar);
    }
    @Override
    public void hideProgressBar() {
        MyUtils.hide(progressBar);
    }

    @Override
    public void showImageProgressBar() {
        MyUtils.show(imageProgressBar);
    }

    @Override
    public void enableForm() {
        titleView.setEnabled(true);
        quoteView.setEnabled(true);
        descriptionView.setEnabled(true);

        discardImageButton.setEnabled(false);
        saveButton.setEnabled(true);
    }
    @Override
    public void disableForm() {
        titleView.setEnabled(false);
        quoteView.setEnabled(false);
        descriptionView.setEnabled(false);

        discardImageButton.setEnabled(false);
        saveButton.setEnabled(false);
    }


    @Override
    public void showInfo(int msgId) {
        showMessage(msgId, Constants.INFO_MSG);
    }
    @Override
    public void showError(int msgId) {
        showMessage(msgId, Constants.ERROR_MSG);
    }
    @Override
    public void hideMessage() {
        MyUtils.hide(messageView);
    }

    private void showMessage(int msgId, String msgType) {
        int colorId;
        switch (msgType) {
            case Constants.INFO_MSG:
                colorId = R.color.info;
                break;
            case Constants.ERROR_MSG:
                colorId = R.color.error;
                break;
            default:
                colorId = R.color.undefined;
                break;
        }
        messageView.setTextColor(getResources().getColor(colorId));

        String msg = getResources().getString(msgId);
        messageView.setText(msg);

        MyUtils.show(messageView);
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    void checkPermissions() {

    }

    private void processInputIntent() {
        Log.d(TAG, "processInputIntent()");

        Intent intent = getIntent();
        Card card = intent.getParcelableExtra(Constants.CARD);
        String cardType = intent.getStringExtra(Constants.CARD_TYPE);

        if (null != card) {
            Log.d(TAG, "Правка");
            presenter.onCardRecieved(card);
        } else {
            Log.d(TAG, "Созидание");
            presenter.onCreateCard(cardType);
        }
    }

    private void displayCommonCardParts(Card card) {
        titleView.setText(card.getTitle());
        descriptionView.setText(card.getDescription());
    }
}
