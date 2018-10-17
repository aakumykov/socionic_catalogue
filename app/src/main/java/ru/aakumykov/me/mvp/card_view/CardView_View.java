package ru.aakumykov.me.mvp.card_view;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

//TODO: уменьшение изображения
//TODO: scrollView

public class CardView_View extends AppCompatActivity implements
        iCardView.View {

    private final static String TAG = "CardView_View";
    private iCardView.Presenter presenter;

    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.messageView) TextView messageView;
    @BindView(R.id.titleView) TextView titleView;
    @BindView(R.id.quoteView) TextView quoteView;
    @BindView(R.id.imageHolder) ConstraintLayout imageHolder;
    @BindView(R.id.imageProgressBar) ProgressBar imageProgressBar;
    @BindView(R.id.imageView) ImageView imageView;
    @BindView(R.id.descriptionView) TextView descriptionView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_view_activity);
        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        presenter = new CardView_Presenter();
        presenter.linkView(this); // Здесь тоже нужен linkView()

        Intent intent = getIntent();
        String cardKey = intent.getStringExtra(Constants.CARD_KEY);

        presenter.cardKeyRecieved(cardKey);
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.linkView(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.unlinkView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.card_view_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.actionEdit:
                presenter.onEditButtonClicked();
                break;
            case R.id.actionDelete:
                presenter.onDeleteButtonClicked();
                break;
            case android.R.id.home:
                this.finish();
                break;
            default:
                super.onOptionsItemSelected(item);
        }

        return true;
    }


    // Ожидание
    @Override
    public void showWaitScreen() {
        showInfoMsg(R.string.opening_card);
        MyUtils.show(progressBar);
    }


    // Карточка
    @Override
    public void displayCard(Card card) {
        Log.d(TAG, "displayCard(), "+card);

        hideWaitScreen();

        switch (card.getType()) {
            case Constants.IMAGE_CARD:
                displayImageCard(card);
                break;

            case Constants.TEXT_CARD:
                displayTextCard(card);
                break;

            default:
                showErrorMsg(R.string.wrong_card_type);
                break;
        }
    }


    // Изображение
    @Override
    public void displayImage(Uri imageURI) {
        Log.d(TAG, "displayImage("+imageURI+")");

        MyUtils.show(imageHolder);
        MyUtils.show(imageProgressBar);
        MyUtils.hide(imageView);

        Picasso.get().load(imageURI).into(imageView, new Callback() {
            @Override
            public void onSuccess() {
                MyUtils.hide(imageProgressBar);
                MyUtils.show(imageView);
            }

            @Override
            public void onError(Exception e) {
                showErrorMsg(e.getMessage());
                MyUtils.hide(imageProgressBar);
                displayImageError();
            }
        });
    }

    @Override
    public void displayImageError() {
        imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_image_broken));
    }


    // Сообщения
    @Override
    public void showInfoMsg(int messageId) {
        showMsg(getResources().getString(messageId), getResources().getColor(R.color.info));
    }

    @Override
    public void showErrorMsg(int messageId) {
        showErrorMsg(getResources().getString(messageId));
    }

    @Override
    public void showErrorMsg(String message) {
        showMsg(message, getResources().getColor(R.color.error));
    }

    @Override
    public void hideMsg() {
        MyUtils.hide(messageView);
    }

    @Override
    public void showProgressMessage(int messageId) {
        showInfoMsg(messageId);
        MyUtils.show(progressBar);
    }

    @Override
    public void hideProgressMessage() {
        hideMsg();
        MyUtils.hide(progressBar);
    }


    // Переходы
    @Override
    public void goEditPage(Card card) {

    }

    @Override
    public void closePage() {

    }


    // Внутренние методы
    private void showMsg(String text, int color) {
        messageView.setText(text);
        messageView.setTextColor(color);
        hideWaitScreen();
        MyUtils.show(messageView);
    }

    private void hideWaitScreen() {
        MyUtils.hide(messageView);
        MyUtils.hide(progressBar);
    }

    private void displayImageCard(Card card) {
        Log.d(TAG, "displayImageCard(), "+card);

        displayCommonCard(card);

        try {
            Uri imageURI = Uri.parse(card.getImageURL());
            displayImage(imageURI);
        } catch (Exception e) {
            displayImageError();
        }
    }

    private void displayTextCard(Card card) {
        Log.d(TAG, "displayTextCard(), "+card);
        quoteView.setText(card.getQuote());
        displayCommonCard(card);
    }

    private void displayCommonCard(Card card) {
        titleView.setText(card.getTitle());
        descriptionView.setText(card.getDescription());
    }

}
