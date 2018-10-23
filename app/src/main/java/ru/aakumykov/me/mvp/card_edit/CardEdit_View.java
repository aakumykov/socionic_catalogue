package ru.aakumykov.me.mvp.card_edit;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
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

import java.util.concurrent.Callable;

import butterknife.BindView;
import butterknife.ButterKnife;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;
import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.MyUtils;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.interfaces.iCardsService;
import ru.aakumykov.me.mvp.models.Card;
import ru.aakumykov.me.mvp.services.CardsService;

// TODO: кнопка Вверх как Отмена
// TODO: сохранение при ещё не загруженной картинке


@RuntimePermissions
public class CardEdit_View extends AppCompatActivity
        implements iCardEdit.View, View.OnClickListener {

    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.messageView) TextView messageView;
    @BindView(R.id.titleView) EditText titleView;
    @BindView(R.id.quoteView) EditText quoteView;

    @BindView(R.id.imageHolder) ConstraintLayout imageHolder;
    @BindView(R.id.imageView) ImageView imageView;
    @BindView(R.id.discardImageButton) ImageView discardImageButton;
    @BindView(R.id.imageProgressBar) ProgressBar imageProgressBar;

    @BindView(R.id.descriptionView) EditText descriptionView;
    @BindView(R.id.saveButton) Button saveButton;
    @BindView(R.id.cancelButton) Button cancelButton;


    private final static String TAG = "CardEdit_View";

    private Intent cardsServiceIntent;
    private ServiceConnection cardsServiceConnection;
    private Callable onServiceConnected;
    private Callable onServiceDisconnected;
    private iCardsService cardsService;
    private boolean isCardsServiceBounded = false;
    
    private iCardEdit.Presenter presenter;

    private boolean firstRun = true;


    // Системные методы
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_edit_activity);
        ButterKnife.bind(this);

        // Настройка элементов интерфейса
        saveButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        discardImageButton.setOnClickListener(this);
        imageView.setOnClickListener(this);

        // Запрос разрешений
        CardEdit_ViewPermissionsDispatcher.checkPermissionsWithPermissionCheck(this);

        // Создание Презентатора
        presenter = new CardEdit_Presenter();
        
        // Соединение со службой
        cardsServiceIntent = new Intent(this, CardsService.class);

        onServiceConnected = new Callable() {
            @Override
            public Void call() throws Exception {
                presenter.linkView(CardEdit_View.this);
                presenter.linkModel(cardsService);
                if (firstRun) processInputIntent();
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
        Log.d(TAG, "onStart()");
        super.onStart();
        bindService(cardsServiceIntent, cardsServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop()");
        super.onStop();
        if (isCardsServiceBounded)
            unbindService(cardsServiceConnection);
    }

    @Override
    public void onClick(View v) {
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
            default:
                Log.e(TAG, "Clicked element with unknown id: "+v.getId());
        }
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
        Log.d(TAG, "displayTextCard()");
        displayCommonCardParts(card);
        showQuote(card);
    }

    @Override
    public void displayImageCard(Card card) {
        Log.d(TAG, "displayImageCard()");
        displayCommonCardParts(card);
        showImage(card.getImageURL());
    }

    private void displayCommonCardParts(Card card) {
        titleView.setText(card.getTitle());
        descriptionView.setText(card.getDescription());
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
    public void prepareForTextCard() {
        MyUtils.show(quoteView);
    }

    @Override
    public void prepareForImageCard() {
        MyUtils.show(imageHolder);
        MyUtils.hide(imageProgressBar);
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
//        presenter.linkModel(cardsService);

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


    // Сообщения пользователю
    @Override
    public void showInfoMsg(int messageId) {
        showMsg(getResources().getString(messageId), getResources().getColor(R.color.info));
    }

    @Override
    public void showErrorMsg(int messageId) {
        String msg = getResources().getString(messageId);
        showErrorMsg(msg);
        Log.e(TAG, msg);
    }

    @Override
    public void showErrorMsg(String message) {
        showMsg(message, getResources().getColor(R.color.error));
    }

    private void showMsg(String text, int color) {
        messageView.setText(text);
        messageView.setTextColor(color);
        MyUtils.show(messageView);
    }

    @Override
    public void hideMsg() {
        MyUtils.hide(messageView);
    }


    // Служебные методы
    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    void checkPermissions() {

    }

    private void processInputIntent() {
        Log.d(TAG, "processInputIntent()");

        firstRun = false;

        hideWating();

        Intent intent = getIntent();
        Card card = intent.getParcelableExtra(Constants.CARD);
        String cardType = intent.getStringExtra(Constants.CARD_TYPE);

        if (null != card) {
            Log.d(TAG, "Правка");
            presenter.editCard(card);
        } else {
            Log.d(TAG, "Созидание");
            presenter.createCard(cardType);
        }
    }

}
