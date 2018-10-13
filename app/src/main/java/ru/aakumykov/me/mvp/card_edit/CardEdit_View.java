package ru.aakumykov.me.mvp.card_edit;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.MyUtils;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.models.Card;

// TODO: кнопка Вверх как Отмена

public class CardEdit_View extends AppCompatActivity
        implements iCardEdit.View {

    private final static String TAG = "CardEdit_View";

    @BindView(R.id.errorView) TextView errorView;
    @BindView(R.id.titleView) TextView titleView;
    @BindView(R.id.quoteView) TextView quoteView;
    @BindView(R.id.descriptionView) TextView descriptionView;
    @BindView(R.id.saveButton) Button saveButton;
    @BindView(R.id.cancelButton) Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        iCardEdit.ViewModel viewModel = ViewModelProviders.of(this).get(CardEdit_ViewModel.class);

        LiveData<Card> cardLiveData = viewModel.getCardLiveData();
        cardLiveData.observe(this, new Observer<Card>() {
            @Override
            public void onChanged(@Nullable Card card) {
                fillEditForm(card);
            }
        });

        LiveData<String> errorLiveData = viewModel.getErrorLiveData();
        errorLiveData.observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                showError(s);
            }
        });
    }

    @Override
    public void fillEditForm(Card card) {
        Log.d(TAG, "fillEditForm(), "+card);
        titleView.setText(card.getTitle());
        quoteView.setText(card.getQuote());
        descriptionView.setText(card.getDescription());
        enableSaveButton();
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
