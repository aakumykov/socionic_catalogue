package ru.aakumykov.me.mvp.card_edit;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.MyUtils;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.models.Card;

// TODO: кнопка Вверх как Отмена

public class CardEdit_View extends AppCompatActivity
        implements iCardEdit.View, iCardEdit.ModelCallbacks {

    private final static String TAG = "CardEdit_View";
    private Card currentCard;
    private iCardEdit.Model model = CardEdit_Model.getInstance();

    @BindView(R.id.messageView) TextView messageView;
    @BindView(R.id.titleView) EditText titleView;
    @BindView(R.id.quoteView) EditText quoteView;
    @BindView(R.id.descriptionView) EditText descriptionView;
    @BindView(R.id.saveButton) Button saveButton;
    @BindView(R.id.cancelButton) Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_edit_activity);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        Card card = intent.getParcelableExtra(Constants.CARD);
        Log.d(TAG, "card из Intent: "+card);
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
        quoteView.setText(card.getQuote());

        enableSaveButton();
    }


    // TODO: сохранение в виде транзакции (и/или блокировка на время правки)
    @OnClick(R.id.saveButton)
    public void saveCard() {
        Log.d(TAG, "saveCard()");
        currentCard.setTitle(titleView.getText().toString());
        currentCard.setQuote(quoteView.getText().toString());
        currentCard.setDescription(descriptionView.getText().toString());
        model.saveCard(currentCard, this);
    }

    @Override
    public void onSaveSuccess() {
        Log.d(TAG, "onSaveSuccess()");

    }

    @Override
    public void onSaveError(String message) {
        Log.d(TAG, "onSaveError()");
        showMessage(R.string.savingError, Constants.ERROR_MSG);
    }

    @Override
    public void onSaveCancel() {
        showMessage(R.string.savingCancel, Constants.ERROR_MSG);
    }


    @OnClick(R.id.cancelButton)
    @Override
    public void cancelEdit() {
        this.finish();
    }


    @Override
    public void enableSaveButton() {
        saveButton.setEnabled(true);
    }
    @Override
    public void disableSaveButton() {
        saveButton.setEnabled(false);
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

}
