package ru.aakumykov.me.mvp.card_edit;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.MyUtils;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.models.Card;

// TODO: кнопка Вверх как Отмена

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
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_edit_activity);
        ButterKnife.bind(this);

        this.presenter = new CardEdit_Presenter();
        this.presenter.linkView(this);

        saveButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        discardImageButton.setOnClickListener(this);
        localImageView.setOnClickListener(this);

        Intent intent = getIntent();
        Card card = intent.getParcelableExtra(Constants.CARD);
//        Log.d(TAG, "card из Intent: "+card);
        presenter.onCardRecieved(card);
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.presenter.linkView(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.unlinkView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.saveButton:
                presenter.saveButonClicked();
                break;
            case R.id.cancelButton:
                presenter.cancelButtonClicked();
                break;
            case R.id.localImageView:
                presenter.selectImageClicked();
                break;
            case R.id.discardImageButton:
                presenter.imageDiscardClicked();
                break;
            default:
                Log.e(TAG, "Clicked element with unknown id: "+v.getId());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult()");

        switch (requestCode) {
            case Constants.CODE_SELECT_IMAGE:
                processSelectedImage(resultCode, data);
                break;
            default:
                break;
        }
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
        displayImage(card.getImageURL());
    }


    @Override
    public void displayImage(String imageURI) {
        Uri uri = Uri.parse(imageURI);
        if (null != uri) {
            displayImage(uri);
        } else {
            showError(R.string.error_loading_image);
            Log.e(TAG, "Wrong image URI: "+imageURI);
        }
    }

    @Override
    public void displayImage(Uri imageURI) {
//        Log.d(TAG, "displayImage("+imageURI+")");

        MyUtils.show(imageHolder);

        Picasso.get().load(imageURI)
                .into(remoteImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        MyUtils.hide(localImageView);
                        MyUtils.hide(imageProgressBar);
                        MyUtils.show(imageHolder);
                        MyUtils.show(remoteImageView);
                        MyUtils.show(discardImageButton);
                    }

                    @Override
                    public void onError(Exception e) {
                        showError(R.string.error_loading_image);
                        e.printStackTrace();
                    }
                });
    }

    @Override
    public void removeImage() {
        Log.d(TAG, "removeImage()");

        remoteImageView.setImageDrawable(null);
        MyUtils.hide(remoteImageView);

        localImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_picture_placeholder));
        MyUtils.show(localImageView);

        MyUtils.hide(discardImageButton);
//        localImageView.setOnClickListener(this);
    }


    // TODO: запрос разрешений
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

    private void processSelectedImage(int resultCode, @Nullable Intent data) {
        Log.d(TAG, "processSelectedImage()");

        if (RESULT_OK != resultCode) {
            showError(R.string.error_selecting_image);
            return;
        }

        if (null == data) {
            showError(R.string.image_data_error);
            return;
        }

        Uri dataURI = data.getData();
        displayImage(dataURI);
    }


    @Override
    public void closeActivity() {
        finish();
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
    public void enableForm() {
        titleView.setEnabled(true);
        quoteView.setEnabled(true);
        descriptionView.setEnabled(true);
        saveButton.setEnabled(true);
    }
    @Override
    public void disableForm() {
        titleView.setEnabled(false);
        quoteView.setEnabled(false);
        descriptionView.setEnabled(false);
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


    private void displayCommonCardParts(Card card) {
        titleView.setText(card.getTitle());
        descriptionView.setText(card.getDescription());
    }
}
