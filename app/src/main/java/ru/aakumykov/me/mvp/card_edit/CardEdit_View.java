package ru.aakumykov.me.mvp.card_edit;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.security.SecureRandom;
import java.security.SecureRandomSpi;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.MyUtils;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.models.Card;

// TODO: кнопка Вверх как Отмена

public class CardEdit_View extends AppCompatActivity
        implements iCardEdit.View, iCardEdit.ModelCallbacks {

    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.messageView) TextView messageView;
    @BindView(R.id.titleView) EditText titleView;
    @BindView(R.id.quoteView) EditText quoteView;
    @BindView(R.id.imageHolder) ConstraintLayout imageHolder;
    @BindView(R.id.imageView) ImageView imageView;
    @BindView(R.id.imageProgressBar) ProgressBar imageProgressBar;
    @BindView(R.id.imageDeleteButton) ImageButton imageDeleteButton;
    @BindView(R.id.descriptionView) EditText descriptionView;
    @BindView(R.id.saveButton) Button saveButton;
    @BindView(R.id.cancelButton) Button cancelButton;


    private final static String TAG = "CardEdit_View";
    private iCardEdit.Model model = CardEdit_Model.getInstance();
    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

    private Card currentCard;
    private String oldImageURL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_edit_activity);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        Card card = intent.getParcelableExtra(Constants.CARD);
//        Log.d(TAG, "card из Intent: "+card);
        if (null != card) {
            currentCard = card;
            fillEditForm(card);
        }
    }

    @Override
    public void fillEditForm(Card card) {
        Log.d(TAG, "fillEditForm(), "+card);

        descriptionView.setText(card.getDescription());
        titleView.setText(card.getTitle());

        switch (card.getType()) {

            case Constants.TEXT_CARD:
                Log.d(TAG, "текстовая карточка");
                quoteView.setText(card.getQuote());
                MyUtils.show(quoteView);
                break;

            case Constants.IMAGE_CARD:
                Log.d(TAG, "графическая карточка");

                MyUtils.show(imageHolder);

                loadImage(card.getImageURL(), new Callback() {
                    @Override
                    public void onSuccess() {
                        MyUtils.hide(imageProgressBar);
                        MyUtils.show(imageDeleteButton);
                    }

                    @Override
                    public void onError(Exception e) {
                        showError(R.string.error_loading_image);
                    }
                });
                break;

            default:
                showMessage(R.string.error_displaying_card, Constants.ERROR_MSG);
                disableEditForm();
                Log.e(TAG, "Unknown card type: "+card.getType());
                break;
        }

        enableEditForm();
    }

    @OnClick(R.id.imageDeleteButton)
    void deleteImage() {
        Log.d(TAG, "deleteImage()");
        oldImageURL = currentCard.getImageURL();
        currentCard.setImageURL(null);

        imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_picture_placeholder));
        MyUtils.hide(imageDeleteButton);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slectImage();
            }
        });
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    private void slectImage() {
//        Log.d(TAG, "selectImage()");
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

        if (RESULT_OK == resultCode) {
            switch (requestCode) {
                case Constants.CODE_SELECT_IMAGE:
                    if (null != data) {
                        Uri dataURI = data.getData();
                        displayLocalImage(dataURI);
                    }
                    break;
                default:
                    Log.w(TAG, "Unknown request code: "+requestCode);
            }
        } else {
            Log.w(TAG, "Unsuccessful result code");
        }
    }



    // TODO: сохранение в виде транзакции (и/или блокировка на время правки)
    @OnClick(R.id.saveButton)
    public void saveCard() {
        Log.d(TAG, "saveCard()");

        currentCard.setTitle(titleView.getText().toString());
        currentCard.setQuote(quoteView.getText().toString());
        currentCard.setDescription(descriptionView.getText().toString());

        disableEditForm();
        showMessage(R.string.saving_card, Constants.INFO_MSG);
        showProgressBar();

        model.saveCard(currentCard, this);
    }

    @Override
    public void onSaveSuccess() {
        Log.d(TAG, "onSaveSuccess()");
        Intent intent = new Intent();
        intent.putExtra(Constants.CARD, currentCard);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onSaveError(String message) {
        Log.d(TAG, "onSaveError()");
        hideProgressBar();
        enableEditForm();
        showMessage(R.string.saving_error, Constants.ERROR_MSG);
    }

    @Override
    public void onSaveCancel() {
        hideProgressBar();
        enableEditForm();
        showMessage(R.string.saving_cancel, Constants.ERROR_MSG);
    }


    // TODO: отмена на уровне Firebase...
    @OnClick(R.id.cancelButton)
    @Override
    public void cancelEdit() {
        this.finish();
    }


    @Override
    public void enableEditForm() {
        titleView.setEnabled(true);
        quoteView.setEnabled(true);
        descriptionView.setEnabled(true);
        saveButton.setEnabled(true);
    }
    @Override
    public void disableEditForm() {
        titleView.setEnabled(false);
        quoteView.setEnabled(false);
        descriptionView.setEnabled(false);
        saveButton.setEnabled(false);
    }

    @Override
    public void showProgressBar() {
        MyUtils.show(progressBar);
    }

    @Override
    public void hideProgressBar() {
        MyUtils.hide(progressBar);
    }



    private void showInfo(int messageId) {
        showMessage(messageId, Constants.INFO_MSG);
    }

    private void showError(int messageId) {
        showMessage(messageId, Constants.ERROR_MSG);
    }

    @Override
    public void showMessage(int msgId, String msgType) {
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
    @Override
    public void hideMessage() {
        MyUtils.hide(messageView);
    }



    private void loadImage(String imageURL, final Callback callback) {
        Log.d(TAG, "loadImage("+imageURL+")");
        Uri imageURI = Uri.parse(imageURL);

        Picasso.get()
                .load(imageURI)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        callback.onSuccess();
                    }

                    @Override
                    public void onError(Exception e) {
                        callback.onError(e);
                        e.printStackTrace();
                    }
                });

    }

    private void displayLocalImage(Uri imageURI) {
        Log.d(TAG, "displayLocalImage("+imageURI+")");
        Picasso.get().load(imageURI).into(imageView);
    }

    private void uploadImage(Uri imageURI, final Callback callback) {
        Log.d(TAG, "uploadImage()");

        // TODO: разобраться с типом файла
        String fileName = currentCard.getKey() + ".jpg";

        StorageReference imageRef = firebaseStorage.getReference()
                .child(Constants.IMAGES_PATH);

        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpeg")
                .build();

        UploadTask uploadTask = imageRef.putFile(imageURI, metadata);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        callback.onSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onError(e);
                    }
                })
                .addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        Log.w(TAG, "Image upload cancelled");
                        deleteImage();
                    }
                });
    }
}
