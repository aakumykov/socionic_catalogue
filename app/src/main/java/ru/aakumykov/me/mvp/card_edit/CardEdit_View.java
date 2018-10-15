package ru.aakumykov.me.mvp.card_edit;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;

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
    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_edit_activity);
        ButterKnife.bind(this);

        this.presenter = new CardEdit_Presenter();

        saveButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        discardImageButton.setOnClickListener(this);
        localImageView.setOnClickListener(this);

        Intent intent = getIntent();
        Card card = intent.getParcelableExtra(Constants.CARD);
        Log.d(TAG, "card из Intent: "+card);
//        String cardKey = intent.getStringExtra(Constants.CARD_KEY);
        presenter.cardRecieved(card);
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
                presenter.selectImageButtonClicked();
                break;
            case R.id.discardImageButton:
                presenter.imageDiscardButtonClicked();
                break;
            default:
                Log.e(TAG, "Clicked element with unknown id: "+v.getId());
        }
    }


//    @Override
//    public void fillEditForm(Card card) {
//        Log.d(TAG, "fillEditForm(), "+card);
//
//        descriptionView.setText(card.getDescription());
//        titleView.setText(card.getTitle());
//
//        switch (card.getType()) {
//
//            case Constants.TEXT_CARD:
//                Log.d(TAG, "текстовая карточка");
//                quoteView.setText(card.getQuote());
//                MyUtils.show(quoteView);
//                break;
//
//            case Constants.IMAGE_CARD:
//                Log.d(TAG, "графическая карточка");
//                break;
//
//            default:
//                showMessage(R.string.error_displaying_card, Constants.ERROR_MSG);
//                disableForm();
//                Log.e(TAG, "Unknown card type: "+card.getType());
//                break;
//        }
//
//        enableForm();
//    }


    @Override
    public void setTitle(String title) {

    }

    @Override
    public void setQuote(String quote) {

    }

    @Override
    public void setDescription(String description) {

    }


    @Override
    public void displayImage(Uri imageUI) {

    }
    @Override
    public void removeImage() {

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

}
