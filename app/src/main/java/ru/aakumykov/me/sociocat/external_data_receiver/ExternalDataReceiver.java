package ru.aakumykov.me.sociocat.external_data_receiver;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.Menu;

import ru.aakumykov.me.sociocat.BaseView;
import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_edit.CardEdit_View;
import ru.aakumykov.me.sociocat.card_show.CardShow_View;
import ru.aakumykov.me.sociocat.models.Card;

public class ExternalDataReceiver extends BaseView {

    public static final String TAG = "ExternalDataReceiver";

    // Системные методы
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_reciever_activity);

        setPageTitle(R.string.EXTERNAL_DATA_RECIEVER_page_title);

        try {
            makeStartDecision();
        } catch (Exception e) {
            showErrorMsg(R.string.EXTERNAL_DATA_RECIEVER_error_starting_work, e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onUserLogin() {

    }

    @Override
    public void onUserLogout() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        switch (requestCode) {
            case Constants.CODE_CREATE_CARD:
                try {
                    processCardCreationResult(resultCode, data);
                } catch (Exception e) {
                    showErrorMsg(R.string.EXTERNAL_DATA_RECIEVER_error_creating_card, e.getMessage());
                    e.printStackTrace();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    // Внутренние методы
    private void makeStartDecision() throws Exception {

        Intent inputEntent = getIntent();
        if (null == inputEntent)
            throw new IllegalArgumentException("Input intent is NULL");

        Intent outputIntent = new Intent(inputEntent);
        outputIntent.setClass(this, CardEdit_View.class);
        outputIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        startActivityForResult(outputIntent, Constants.CODE_CREATE_CARD);
    }

    private void processCardCreationResult(int resultCode, @Nullable Intent data) {
        if (null == data) {
            throw  new IllegalArgumentException("Intent is null");
        }

        if (RESULT_CANCELED == resultCode) {
            finish();
            return;
        }

        Card card = data.getParcelableExtra(Constants.CARD);
        Intent intent = new Intent(this, CardShow_View.class);
        intent.putExtra(Constants.CARD, card);
        startActivity(intent);
    }
}
