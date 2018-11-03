package ru.aakumykov.me.mvp.card_edit;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
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
import ru.aakumykov.me.mvp.BaseView;
import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.utils.MyUtils;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.models.Card;

// TODO: кнопка Вверх как Отмена
// TODO: сохранение при ещё не загруженной картинке


@RuntimePermissions
public class CardEdit_View extends BaseView implements
        iCardEdit.View,
        View.OnClickListener,
        TagView.OnTagClickListener
{
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.messageView) TextView messageView;

    @BindView(R.id.titleView) EditText titleView;
    @BindView(R.id.quoteView) EditText quoteView;
    @BindView(R.id.imageHolder) ConstraintLayout imageHolder;
    @BindView(R.id.imageView) ImageView imageView;
    @BindView(R.id.discardImageButton) ImageView discardImageButton;
    @BindView(R.id.imageProgressBar) ProgressBar imageProgressBar;
    @BindView(R.id.descriptionView) EditText descriptionView;

    @BindView(R.id.tagsContainer) TagContainerLayout tagsContainer;
    @BindView(R.id.newTagInput) EditText newTagInput;
    @BindView(R.id.addTagButton) Button tagAddButton;

    @BindView(R.id.saveButton) Button saveButton;
    @BindView(R.id.cancelButton) Button cancelButton;


    private final static String TAG = "CardEdit_View";
    private iCardEdit.Presenter presenter;


    // Системные методы
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_edit_activity);
        ButterKnife.bind(this);

        disableForm();
        tagsContainer.setOnTagClickListener(this);

        // Запрос разрешений
        CardEdit_ViewPermissionsDispatcher.checkPermissionsWithPermissionCheck(this);

        // Создание Презентатора
        presenter = new CardEdit_Presenter();
    }

    @Override
    public void onServiceBounded() {
//        Log.d(TAG, "onServiceBounded()");
        presenter.linkView(this);
        presenter.linkCardsService(getCardsService());

        try {
            presenter.processInputIntent(getIntent());
        } catch (Exception e) {
            showErrorMsg(R.string.CARD_EDIT_error_editing_card, e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onServiceUnbounded() {
//        Log.d(TAG, "onServiceUnbounded()");
        presenter.unlinkView();
        presenter.unlinkCardsService();
    }


    @OnClick({
        R.id.saveButton,
        R.id.cancelButton,
        R.id.discardImageButton,
        R.id.imageView,
        R.id.addTagButton
    })
    @Override
    public void onClick(View v) {
//        Log.d(TAG,"onClick("+v.getId()+")");

        switch (v.getId()) {

            case R.id.saveButton:
                presenter.onSaveButtonClicked();
                break;

            case R.id.cancelButton:
                presenter.onCancelButtonClicked();
                break;

            case R.id.imageView:
                presenter.onSelectImageClicked();
                break;

            case R.id.discardImageButton:
                presenter.onImageDiscardClicked();
                break;

            case R.id.addTagButton:
                presenter.onAddTagButtonClicked();
                break;

            default:
                Log.e(TAG, "Clicked element with unknown id: "+v.getId());
        }
    }

    @Override
    public void onTagClick(int position, String text) {

    }

    @Override
    public void onTagLongClick(int position, String text) {

    }

    @Override
    public void onTagCrossClick(int position) {
//        Log.d(TAG, "onTagCrossClick(), position: "+position);
        tagsContainer.removeTag(position);
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


    // Индикатор загрузки
    @Override
    public void showWating() {
        MyUtils.show(progressBar);
    }

    @Override
    public void hideWating() {
        MyUtils.hide(progressBar);
    }


    // Отображение карточки
    @Override
    public void displayTextCard(Card card) {
        Log.d(TAG, "displayTextCard(), "+card);
        displayCommonCardParts(card);
        showQuote(card);
    }

    @Override
    public void displayImageCard(Card card) {
        Log.d(TAG, "displayImageCard(), "+card);
        displayCommonCardParts(card);
        showImage(card.getImageURL());
    }



    // Показ картинки
    @Override
    public void showImage(String imageURI) {
        try {
            Uri uri = Uri.parse(imageURI);
            showImage(uri);
        } catch (Exception e) {
            showErrorMsg(R.string.error_loading_image);
            showBrokenImage();
        }
    }

    @Override
    public void showImage(Uri imageURI) {
        hideMsg();
        MyUtils.show(imageHolder);
        MyUtils.show(imageProgressBar);

        Picasso.get().load(imageURI)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        MyUtils.hide(imageProgressBar);
                        MyUtils.show(imageView);
                        MyUtils.show(discardImageButton);
                    }

                    @Override
                    public void onError(Exception e) {
                        MyUtils.hide(imageProgressBar);
                        showBrokenImage();
                        showErrorMsg(R.string.error_loading_image);
                        e.printStackTrace();
                    }
                });
    }

    @Override
    public void removeImage() {
        Log.d(TAG, "removeImage()");
        imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_image_placeholder));
        MyUtils.hide(discardImageButton);
    }

    @Override
    public void showBrokenImage() {
        imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_image_broken));
        MyUtils.hide(discardImageButton);
    }


    // Показ цитаты
    private void showQuote(Card card) {
        quoteView.setText(card.getQuote());
        MyUtils.show(quoteView);
    }


    // Подготовка формы для новой карточки
    @Override
    public void prepareTextCardForm(Card cardDraft) {
        quoteView.setText(cardDraft.getQuote());
        MyUtils.show(quoteView);
    }

    @Override
    public void prepareImageCardForm(Card cardDraft) {
        MyUtils.show(imageHolder);
        MyUtils.hide(imageProgressBar);
        String imageURL = cardDraft.getImageURL();
        if (null != imageURL) {
            showImage(imageURL);
        }
    }

    @Override
    public void addTag(String tagName) {
//        Log.d(TAG, "addTag("+tagName+")");
        tagsContainer.addTag(tagName);
    }


    // Выбор картинки
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
        Log.d(TAG, "onActivityResult(requestCode: "+requestCode+", resultCode: "+resultCode+", ...)");

//        presenter.linkView(this);
//        presenter.linkCardsService(cardsService);

        if (RESULT_CANCELED == resultCode) {
            Log.d(TAG, "Выбор картинки отменён");
            return;
        }

        if (null == data) {
            showErrorMsg(R.string.data_error);
            Log.e(TAG, "Нет данных (null) в результатах выбора");
            return;
        }

        Uri dataURI = data.getData();
        Log.d(TAG, "dataURI: "+dataURI);

        switch (requestCode) {
            case Constants.CODE_SELECT_IMAGE:
                processSelectedImage(dataURI);
                break;
            default:
                break;
        }
    }

    private void processSelectedImage(Uri dataURI) {
        Log.d(TAG, "processSelectedImage()");
        String mimeType =  this.getContentResolver().getType(dataURI);
        presenter.onImageSelected(dataURI, mimeType);
    }


    // Получение данных со страницы
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
    public HashMap<String,Boolean> getCardTags() {
        HashMap<String,Boolean> map = new HashMap<>();
        List<String> tagsList = tagsContainer.getTags();
        for (String tagName : tagsList) {
            map.put(tagName, true);
        }
        return map;
    }

    @Override
    public String getNewTag() {
        return newTagInput.getText().toString();
    }

    @Override
    public void clearNewTag() {
        newTagInput.setText("");
    }

    @Override
    public void focusTagInput() {
        newTagInput.requestFocus();
    }

    // Активация / дизактивация формы
    @Override
    public void enableForm() {
        titleView.setEnabled(true);
        quoteView.setEnabled(true);
        descriptionView.setEnabled(true);

        MyUtils.hide(imageProgressBar);

//        discardImageButton.setEnabled(false);
        saveButton.setEnabled(true);
    }
    @Override
    public void disableForm() {
        titleView.setEnabled(false);
        quoteView.setEnabled(false);
        descriptionView.setEnabled(false);

        MyUtils.show(imageProgressBar);

//        discardImageButton.setEnabled(false);
        saveButton.setEnabled(false);
    }


    // Завершение редактирования
    @Override
    public void finishEdit(Card card) {
        Log.d(TAG, "finishEdit(), "+card);
        Intent intent = new Intent();
        int resultCode = (null != card) ? RESULT_OK : RESULT_CANCELED;
        setResult(resultCode, intent);
        intent.putExtra(Constants.CARD, card);
        finish();
    }


    // Служебные методы
    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    void checkPermissions() {

    }

    // TODO: контроль размера изображения

    // Внутренние методы
    private void displayCommonCardParts(Card card) {
        titleView.setText(card.getTitle());
        descriptionView.setText(card.getDescription());
        showTags(card.getTags());
    }

    private void showTags(HashMap<String,Boolean> tagsMap) {
        Log.d(TAG, "showTags(), "+tagsMap);
        if (null != tagsMap) {
            List<String> tagsList = new ArrayList<>(tagsMap.keySet());
            tagsContainer.setTags(tagsList);
            tagsContainer.setEnableCross(true);
        }
    }



}
