package ru.aakumykov.me.mvp.card_view;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
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
import ru.aakumykov.me.mvp.BaseView;
import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.MyUtils;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.card_edit.CardEdit_View;
import ru.aakumykov.me.mvp.interfaces.iDialogCallbacks;
import ru.aakumykov.me.mvp.models.Card;
import ru.aakumykov.me.mvp.utils.YesNoDialog;

//TODO: уменьшение изображения

public class CardView_View extends BaseView implements
        iCardView.View
{
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.messageView) TextView messageView;
    @BindView(R.id.titleView) TextView titleView;
    @BindView(R.id.quoteView) TextView quoteView;
    @BindView(R.id.imageHolder) ConstraintLayout imageHolder;
    @BindView(R.id.imageProgressBar) ProgressBar imageProgressBar;
    @BindView(R.id.imageView) ImageView imageView;
    @BindView(R.id.descriptionView) TextView descriptionView;

    private final static String TAG = "CardView_View";
    private iCardView.Presenter presenter;
    private boolean firstRun = true;


    // Системные методы
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_view_activity);
        ButterKnife.bind(this);

        presenter = new CardView_Presenter();
    }

    @Override
    public void onServiceBounded() {
        presenter.linkView(this);
        presenter.linkModel(getCardsService());
        presenter.linkAuth(getAuthService());
        loadCard();
    }

    @Override
    public void onServiceUnbounded() {
        presenter.unlinkView();
        presenter.unlinkModel();
        presenter.unlinkAuth();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d(TAG, "onActivityResult("+requestCode+", "+requestCode+", ...), "+data);
        super.onActivityResult(requestCode, resultCode, data);

        presenter.linkView(this); // обязательно

        if (RESULT_OK == resultCode) {

            switch (requestCode) {

                case Constants.CODE_EDIT_CARD:
                    if (null != data) {
                        Card card = data.getParcelableExtra(Constants.CARD);
                        presenter.cardKeyRecieved(card.getKey());
                    } else {
                        showErrorMsg(R.string.error_displaying_card);
                        Log.e(TAG, "Intent data in activity result == null.");
                    }
                    break;

                default:
                    showErrorMsg(R.string.unknown_request_code);
                    Log.d(TAG, "Unknown request code: "+requestCode);
                    break;
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.card_actions_menu, menu);
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
//        showInfoMsg(R.string.opening_card);
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
                showErrorMsg(R.string.error_loading_image);
                e.printStackTrace();
                MyUtils.hide(imageProgressBar);
                displayImageError();
            }
        });
    }

    @Override
    public void displayImageError() {
        imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_image_broken));
    }


    // Индикатор ожидания
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
        Intent intent = new Intent();
        intent.setClass(this, CardEdit_View.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.putExtra(Constants.CARD, card);
        startActivityForResult(intent, Constants.CODE_EDIT_CARD);
    }


    // Внутренние методы
    private void loadCard() {
        if (firstRun) {
            Intent intent = getIntent();
            String cardKey = intent.getStringExtra(Constants.CARD_KEY);
            presenter.cardKeyRecieved(cardKey);
            firstRun = false;
        }
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
        MyUtils.show(quoteView);
        displayCommonCard(card);
    }

    private void displayCommonCard(Card card) {
        titleView.setText(card.getTitle());
        descriptionView.setText(card.getDescription());
    }


    // Диалоги
    @Override
    public void showDeleteDialog() {

        YesNoDialog yesNoDialog = new YesNoDialog(
                this,
                R.string.card_deletion,
                R.string.really_delete_card,
                new iDialogCallbacks.onCheck() {
                    @Override
                    public boolean doCheck() {
                        return true;
                    }
                },
                new iDialogCallbacks.onYes() {
                    @Override
                    public void yesAction() {
                        //Log.d(TAG, "yesAction");
                        presenter.onDeleteConfirmed();
                    }
                },
                new iDialogCallbacks.onNo() {
                    @Override
                    public void noAction() {
                        //Log.d(TAG, "noAction");
                    }
                }
        );

        yesNoDialog.show();
    }

}
