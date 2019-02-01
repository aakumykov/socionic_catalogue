package ru.aakumykov.me.sociocat.external_data_receiver;

import android.content.Intent;
import android.os.Bundle;

import ru.aakumykov.me.sociocat.BaseView;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_edit.CardEdit_View;

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
        intent.setClass(this, CardEdit_View.class);
        startActivity(intent);
    }
}
