package ru.aakumykov.me.sociocat.external_data_receiver;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;

import ru.aakumykov.me.sociocat.BaseView;
import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_edit.CardEdit_View;
import ru.aakumykov.me.sociocat.card_show.CardShow_View;
import ru.aakumykov.me.sociocat.models.Card;

public class ExternalDataReceiver extends BaseView {

    public static final String TAG = "ExternalDataReceiver";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_reciever_activity);

        setPageTitle(R.string.EXTERNAL_DATA_RECIEVER_page_title);

        try {
            makeStartDecision();
        } catch (Exception e) {
            showErrorMsg(e.getMessage());
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
                processCardCreationResult(resultCode, data);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }


    private void makeStartDecision() throws Exception {

        Intent intent = getIntent();
        if (null == intent)
            throw new IllegalArgumentException("Intent is NULL");

        String action = intent.getAction();
        if (null == action)
            throw new IllegalArgumentException("There is no action in intent");

        // Устанавливаю флаг NO_HISTORY для страницы редактирования
        if (Intent.ACTION_SEND.equals(action)) {
            //intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        }

        // Пересылаю Intent другой странице
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.setClass(this, CardEdit_View.class);
        startActivityForResult(intent, Constants.CODE_CREATE_CARD);
    }

    private void processCardCreationResult(int resultCode, @Nullable Intent data) {
        if (null == data) {
            throw  new IllegalArgumentException("Intent is null");
        }

        if (RESULT_CANCELED == resultCode) {
            finish();
            return;
        }

//        data.setClass(this, CardShow_View.class);
//        startActivity(data);

        Card card = data.getParcelableExtra(Constants.CARD);
        Intent intent = new Intent(this, CardShow_View.class);
        intent.putExtra(Constants.CARD, card);
        startActivity(intent);
    }
}
