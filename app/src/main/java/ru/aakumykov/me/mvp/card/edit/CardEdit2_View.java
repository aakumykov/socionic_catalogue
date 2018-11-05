package ru.aakumykov.me.mvp.card.edit;

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
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.lujun.androidtagview.TagContainerLayout;
import ru.aakumykov.me.mvp.BaseView;
import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.card.CardEdit2_Presenter;
import ru.aakumykov.me.mvp.card.iCardEdit2;
import ru.aakumykov.me.mvp.models.Card;
import ru.aakumykov.me.mvp.utils.MyUtils;


public class CardEdit2_View extends BaseView implements
    iCardEdit2.View
{
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

    private final static String TAG = "CardEdit2_View";
    private iCardEdit2.Presenter presenter;
    private boolean firstRun = true;


    // Системные методы
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_edit_activity);
        ButterKnife.bind(this);

        setPageTitle(R.string.CARD_EDIT_page_title);
        activateUpButton();

        presenter = new CardEdit2_Presenter();
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.linkView(this);

        if (firstRun) {
            firstRun = false;

            try {
                presenter.processInputIntent(getIntent());
            } catch (Exception e) {
                showErrorMsg(R.string.CARD_EDIT_error_editing_card);
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
        super.onActivityResult(requestCode, resultCode, data);
    }


    // Интерфейсные методы
    @Override
    public void displayCard(Card card) {

        if (null == card) {
            showErrorMsg(R.string.CARD_EDIT_error_editing_card);
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

    @OnClick(R.id.saveButton) @Override
    public void save() {
        try {
            presenter.saveCard();
        } catch (Exception e) {
            showErrorMsg(R.string.CARD_EDIT_error_saving_card, e.getMessage());
            e.printStackTrace();
        }
    }

    @OnClick(R.id.cancelButton)
    @Override
    public void cancel() {
        finishEdit();
    }

    @Override
    public void selectImage() {

    }

    @Override
    public void finishEdit() {
//        setResult();
        finish();
    }



    // Внутренние методы
    private void displayTextCard(Card card) {
        quoteView.setText(card.getQuote());
        displayCommonCardParts(card);
    }

    private void displayImageCard(Card card) {
        displayImage(card.getImageURL());
        displayCommonCardParts(card);
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

    private void displayImage(Uri imageURI) {
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

    private void removeImage() {
        imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_image_placeholder));
        MyUtils.hide(discardImageButton);
    }


    private void showBrokenImage() {
        imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_image_broken));
        MyUtils.hide(discardImageButton);
    }

    private void showTags(HashMap<String,Boolean> tagsMap) {
        Log.d(TAG, "showTags(), "+tagsMap);
        if (null != tagsMap) {
            List<String> tagsList = new ArrayList<>(tagsMap.keySet());
            tagsContainer.setTags(tagsList);
            tagsContainer.setEnableCross(true);
        }
    }


    private void disableForm() {
        titleView.setEnabled(false);
        quoteView.setEnabled(false);
        descriptionView.setEnabled(false);
        saveButton.setEnabled(false);

        MyUtils.show(imageProgressBar);
    }

    private void enableForm() {
        titleView.setEnabled(true);
        quoteView.setEnabled(true);
        descriptionView.setEnabled(true);
        saveButton.setEnabled(true);

        MyUtils.hide(imageProgressBar);
    }

}
