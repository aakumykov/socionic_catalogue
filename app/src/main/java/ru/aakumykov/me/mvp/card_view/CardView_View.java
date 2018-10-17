package ru.aakumykov.me.mvp.card_view;

import android.content.Intent;
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d(TAG, "onActivityResult()");
        super.onActivityResult(requestCode, resultCode, data);
        presenter.linkView(this);
        presenter.activityResultComes(requestCode, resultCode, data);
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
        MyUtils.show(progressBar);
        showInfoMsg(R.string.opening_card);
    }


    // Карточка
    @Override
    public void displayCard(Card card) {
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

    private void displayImageCard(Card card) {
        try {
            Uri imageURI = Uri.parse(card.getImageURL());
            displayImage(imageURI);
        } catch (Exception e) {
            showErrorMsg(R.string.error_loading_image);
            displayImageError();
        }
    }

    private void displayTextCard(Card card) {
        quoteView.setText(card.getQuote());
        displayCommonCard(card);
    }

    private void displayCommonCard(Card card) {
        titleView.setText(card.getTitle());
        descriptionView.setText(card.getDescription());
    }


    // Изображение
    @Override
    public void displayImage(Uri imageURI) {
        MyUtils.hide(imageView);
        MyUtils.show(imageProgressBar);

        Picasso.get().load(imageURI).into(imageView, new Callback() {
            @Override
            public void onSuccess() {
                MyUtils.show(imageView);
                MyUtils.hide(imageProgressBar);
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
        messageView.setText(getResources().getString(messageId));
        messageView.setTextColor(getResources().getColor(R.color.info));
        MyUtils.hide(progressBar);
    }

    @Override
    public void showErrorMsg(int messageId) {
        String text = getResources().getString(messageId);
        showErrorMsg(text);
    }

    @Override
    public void showErrorMsg(String message) {
        messageView.setText(message);
        messageView.setTextColor(getResources().getColor(R.color.error));
        MyUtils.hide(progressBar);
    }

    @Override
    public void hideMsg() {
        MyUtils.hide(messageView);
    }


    // Переходы
    @Override
    public void goEditPage(Card card) {

    }

    @Override
    public void closePage() {

    }


    //    @Override
//    public void displayCard(Card card) {
//        Log.d(TAG, "displayCard(), "+card);
//        setTitle(card.getTitle());
//        setDescription(card.getDescription());
//
//        switch (card.getType()) {
//            case Constants.TEXT_CARD:
//                setQuote(card.getQuote());
//                break;
//            case Constants.IMAGE_CARD:
//                loadImage(card.getImageURL());
//                break;
//            default:
//                showMessage(R.string.wrong_card_type, Constants.ERROR_MSG);
//                break;
//        }
//    }
//
//    @Override
//    public void setTitle(String title) {
//        titleView.setText(title);
//    }
//
//    @Override
//    public void setQuote(String quote) {
//        quoteView.setText(quote);
//        MyUtils.show(quoteView);
//    }
//
//    @Override
//    public void loadImage(String imageURL) {
////        Log.d(TAG, "loadImage("+imageURL+")");
//
//        MyUtils.show(imageProgressBar);
//
//        if (null == imageURL) {
//            showMessage(R.string.error_missing_image, Constants.ERROR_MSG);
//            MyUtils.hide(imageProgressBar);
//            return;
//        }
//
//        Picasso.get()
//                .load(imageURL)
//                .into(imageView, new Callback() {
//                    @Override
//                    public void onSuccess() {
//                        hideImagePlaceholder();
//                        showImage();
//                    }
//
//                    @Override
//                    public void onError(Exception e) {
//                        showMessage(R.string.error_loading_image, Constants.ERROR_MSG);
//                        showImageIsBroken();
//                    }
//                });
//    }
//
//    @Override
//    public void setDescription(String description) {
//        descriptionView.setText(description);
//    }
//
//
//
//    @Override
//    public void showProgressBar() {
//        MyUtils.show(progressBar);
//    }
//
//    @Override
//    public void hideProgressBar() {
//        MyUtils.hide(progressBar);
//    }
//
//    @Override
//    public void showImagePlaceholder() {
//        imageHolder.setVisibility(View.VISIBLE);
//        imageProgressBar.setVisibility(View.VISIBLE);
//    }
//
//    @Override
//    public void hideImagePlaceholder() {
//        imageProgressBar.setVisibility(View.GONE);
//    }
//
//    @Override
//    public void showQuote() {
//        quoteView.setVisibility(View.VISIBLE);
//    }
//
//    @Override
//    public void showImage() {
//        imageView.setVisibility(View.VISIBLE);
//    }
//
//    @Override
//    public void showImageIsBroken() {
//        imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_image_broken));
//        MyUtils.show(imageView);
//        MyUtils.hide(imageProgressBar);
//    }
//
//    @Override
//    public void showMessage(int msgId, String msgType) {
//        int colorId;
//        switch (msgType) {
//            case Constants.INFO_MSG:
//                colorId = R.color.info;
//                break;
//            case Constants.ERROR_MSG:
//                colorId = R.color.error;
//                break;
//            default:
//                colorId = R.color.undefined;
//                break;
//        }
//        messageView.setTextColor(getResources().getColor(colorId));
//
//        String msg = getResources().getString(msgId);
//        messageView.setText(msg);
//
//        MyUtils.show(messageView);
//    }
//
//    @Override
//    public void hideMsg() {
//        MyUtils.hide(messageView);
//    }
//
//
//    // TODO: что, если перенести в Presenter?
//    @Override
//    public void goEditCard(Card card) {
//        Log.d(TAG, "goEditCard(), "+card);
//
//        Intent intent = new Intent();
//        intent.setClass(this, CardEdit_View.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//        intent.putExtra(Constants.CARD, card);
//
//        startActivityForResult(intent, Constants.CODE_EDIT_CARD);
//    }
//
//    @Override
//    public void close() {
//        this.finish();
//    }
}
