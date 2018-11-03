package ru.aakumykov.me.mvp.card_show;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;
import ru.aakumykov.me.mvp.BaseView;
import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.utils.MyUtils;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.card_edit.CardEdit_View;
import ru.aakumykov.me.mvp.cards_list.CardsList_View;
import ru.aakumykov.me.mvp.models.Card;

//TODO: уменьшение изображения

public class CardShow_View extends BaseView implements
        iCardShow.View,
        TagView.OnTagClickListener
{
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.messageView) TextView messageView;
    @BindView(R.id.titleView) TextView titleView;
    @BindView(R.id.quoteView) TextView quoteView;
    @BindView(R.id.imageHolder) ConstraintLayout imageHolder;
    @BindView(R.id.imageProgressBar) ProgressBar imageProgressBar;
    @BindView(R.id.imageView) ImageView imageView;
    @BindView(R.id.descriptionView) TextView descriptionView;
    @BindView(R.id.tagsContainer) TagContainerLayout tagsContainer;

    private final static String TAG = "CardShow_View";
    private iCardShow.Presenter presenter;
    private boolean firstRun = true;


    // Системные методы
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_show_activity);
        ButterKnife.bind(this);

        tagsContainer.setOnTagClickListener(this);

        presenter = new CardShow_Presenter();
    }

    @Override
    public void onServiceBounded() {
        presenter.linkView(this);
        presenter.linkCardsService(getCardsService());
        presenter.linkAuth(getAuthService());
        loadCard();
    }

    @Override
    public void onServiceUnbounded() {
        presenter.unlinkView();
        presenter.unlinkCardsService();
        presenter.unlinkAuthService();
    }

    @Override
    public void processLogin() {

    }

    @Override
    public void processLogout() {

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
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.edit_delete, menu);
        return super.onCreateOptionsMenu(menu);
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


    @Override
    public void onTagClick(int position, String text) {
        Log.d(TAG, "onTagClick("+position+", "+text+")");
        presenter.onTagClicked(text);
    }

    @Override
    public void onTagLongClick(int position, String text) {

    }

    @Override
    public void onTagCrossClick(int position) {

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

        String pageTitle = getResources().getString(R.string.CARD_SHOW_page_title, card.getTitle());
        setPageTitle(pageTitle);

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
                showErrorMsg(R.string.error_loading_image, e.getMessage());
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

    // Метки
    @Override
    public void showTags(HashMap<String,Boolean> tagsHash) {
//        Log.d(TAG, "showTags(), "+tagsHash);
        if (null != tagsHash) {
            List<String> tagsList = new ArrayList<>(tagsHash.keySet());
            tagsContainer.setTags(tagsList);
            MyUtils.show(tagsContainer);
        }
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
        Log.d(TAG, "goEditPage()");
        Intent intent = new Intent(this, CardEdit_View.class);
        intent.setAction(Intent.ACTION_EDIT);
        intent.putExtra(Constants.CARD, card);
        startActivityForResult(intent, Constants.CODE_EDIT_CARD);
    }

    @Override
    public void goList(@Nullable String tagFilter) {
        Intent intent = new Intent(this, CardsList_View.class);
        if (null != tagFilter)
            intent.putExtra(Constants.TAG_FILTER, tagFilter);
        startActivity(intent);
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
        showTags(card.getTags());
    }


    // Диалоги
    @Override
    public void showDeleteDialog() {

//        YesNoDialog yesNoDialog = new YesNoDialog(
//                this,
//                R.string.DIALOG_card_deletion,
//                R.string.DIALOG_really_delete_card,
//                new iDialogCallbacks.onCheck() {
//                    @Override
//                    public boolean doCheck() {
//                        return true;
//                    }
//                },
//                new iDialogCallbacks.onYes() {
//                    @Override
//                    public void yesAction() {
//                        //Log.d(TAG, "yesAction");
//                        presenter.onDeleteConfirmed();
//                    }
//                },
//                new iDialogCallbacks.onNo() {
//                    @Override
//                    public void noAction() {
//                        //Log.d(TAG, "noAction");
//                    }
//                }
//        );
//
//        yesNoDialog.show();
    }

}
