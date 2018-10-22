package ru.aakumykov.me.mvp.card_view;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.IBinder;
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

import java.util.concurrent.Callable;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.MyUtils;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.card_edit.CardEdit_View;
import ru.aakumykov.me.mvp.interfaces.MyInterfaces;
import ru.aakumykov.me.mvp.models.Card;
import ru.aakumykov.me.mvp.services.CardsService;
import ru.aakumykov.me.mvp.utils.YesNoDialog;

//TODO: уменьшение изображения
//TODO: scrollView

public class CardView_View extends AppCompatActivity implements
        iCardView.View {

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

    private Intent cardsServiceIntent;
    private ServiceConnection cardsServiceConnection;
    private Callable onServiceConnected;
    private Callable onServiceDisconnected;
    private MyInterfaces.CardsService cardsService;
    private boolean isCardsServiceBounded = false;

    private boolean firstRun = true;


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

        // Соединение со службой
        cardsServiceIntent = new Intent(this, CardsService.class);

        onServiceConnected = new Callable() {
            @Override
            public Void call() throws Exception {
                presenter.linkView(CardView_View.this);
                presenter.linkModel(cardsService);
                if (firstRun) {
                    Intent intent = getIntent();
                    String cardKey = intent.getStringExtra(Constants.CARD_KEY);
                    presenter.cardKeyRecieved(cardKey);
                    firstRun = false;
                }
                return null;
            }
        };

        onServiceDisconnected = new Callable() {
            @Override
            public Void call() throws Exception {
                presenter.unlinkView();
                presenter.unlinkModel();
                return null;
            }
        };

        cardsServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d(TAG, "onServiceConnected()");

                CardsService.LocalBinder localBinder = (CardsService.LocalBinder) service;
                cardsService = localBinder.getService();
                isCardsServiceBounded = true;

                try {
                    onServiceConnected.call();
                } catch (Exception e) {
                    showErrorMsg(e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d(TAG, "onServiceDisconnected()");

                isCardsServiceBounded = false;

                try {
                    onServiceDisconnected.call();
                } catch (Exception e) {
                    showErrorMsg(e.getMessage());
                    e.printStackTrace();
                }
            }
        };

    }

    @Override
    protected void onStart() {
        super.onStart();
        bindService(cardsServiceIntent, cardsServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isCardsServiceBounded)
            unbindService(cardsServiceConnection);
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
        Intent intent = new Intent();
        intent.setClass(this, CardEdit_View.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.putExtra(Constants.CARD, card);
        startActivityForResult(intent, Constants.CODE_EDIT_CARD);
    }

    @Override
    public void closePage() {
        finish();
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
                new MyInterfaces.DialogCallbacks.onCheck() {
                    @Override
                    public boolean doCheck() {
                        return true;
                    }
                },
                new MyInterfaces.DialogCallbacks.onYes() {
                    @Override
                    public void yesAction() {
                        //Log.d(TAG, "yesAction");
                        presenter.onDeleteConfirmed();
                    }
                },
                new MyInterfaces.DialogCallbacks.onNo() {
                    @Override
                    public void noAction() {
                        //Log.d(TAG, "noAction");
                    }
                }
        );

        yesNoDialog.show();
    }

}
