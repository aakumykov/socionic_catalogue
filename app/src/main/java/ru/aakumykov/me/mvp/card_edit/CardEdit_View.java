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
import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.MyUtils;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.models.Card;

// TODO: кнопка Вверх как Отмена

public class CardEdit_View extends AppCompatActivity
        implements iCardEdit.View {

    private final static String TAG = "CardEdit_View";

    @BindView(R.id.errorView) TextView errorView;
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
            fillEditForm(card);
        }

//        iCardEdit.ViewModel viewModel = ViewModelProviders.of(this).get(CardEdit_ViewModel.class);
//
//        LiveData<Card> cardLiveData = viewModel.getCardLiveData();
//        cardLiveData.observe(this, new Observer<Card>() {
//            @Override
//            public void onChanged(@Nullable Card card) {
//                Log.d(TAG, "поступили cardLiveData: "+card);
//                fillEditForm(card);
//            }
//        });
//
//        LiveData<String> errorLiveData = viewModel.getErrorLiveData();
//        errorLiveData.observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                Log.d(TAG, "поступили errorLiveData: "+s);
//                Log.d(TAG, "onCreate()");
//                showError(s);
//            }
//        });
    }

    @Override
    public void fillEditForm(Card card) {
        Log.d(TAG, "fillEditForm(), "+card);

        descriptionView.setText(card.getDescription());
        titleView.setText(card.getTitle());
        quoteView.setText(card.getQuote());

        enableSaveButton();
    }

    void saveCard() {
        
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
    public void showError(String msg) {
        errorView.setText(msg);
    }

    @Override
    public void hideError() {
        MyUtils.hide(errorView);
    }
}
