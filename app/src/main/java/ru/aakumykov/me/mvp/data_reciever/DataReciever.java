package ru.aakumykov.me.mvp.data_reciever;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import butterknife.ButterKnife;
import ru.aakumykov.me.mvp.BaseView;
import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.card.edit.CardEdit_View;

public class DataReciever extends BaseView {

    public static final String TAG = "DataReciever";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_reciever_activity);

        setPageTitle(R.string.DATA_RECIEVER_page_title);

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
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        }

        // Пересылаю Intent другой странице
        intent.setClass(this, CardEdit_View.class);
        startActivity(intent);
    }
}
