package ru.aakumykov.me.mvp.test;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.models.Card;
import ru.aakumykov.me.mvp.models.User;

public class Test1Activity extends AppCompatActivity {

    @BindView(R.id.userButton) Button userButton;
    @BindView(R.id.cardButton) Button cardButton;

    private final static String TAG = "Test-1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test1);
        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setTitle("onActivityResult()");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (RESULT_CANCELED == resultCode) {
            Log.d(TAG, "Отменено");
            return;
        }

        switch (requestCode) {
            case 10:
                processCard(data);
                break;
            case 20:
                processUser(data);
                break;
            default:
                Log.e(TAG, "Неизвестный код запроса: "+requestCode);
        }
    }


    @OnClick(R.id.cardButton)
    void testCard() {
        Intent intent = new Intent(this, Test2Activity.class);
        intent.setAction("CARD");
        startActivityForResult(intent, 10);
    }

    @OnClick(R.id.userButton)
    void testUser() {
        Intent intent = new Intent(this, Test2Activity.class);
        intent.setAction("USER");
        startActivityForResult(intent, 20);
    }


    private void processCard(Intent intent) {
        Log.d(TAG, "processCard()");

        Log.d(TAG, "intent: "+intent);

        Card card = intent.getParcelableExtra("CARD");
        Log.d(TAG, "card: "+card);
    }


    private void processUser(Intent intent) {
        Log.d(TAG, "processUser()");

        Log.d(TAG, "intent: "+intent);

        User user = intent.getParcelableExtra("USER");
        Log.d(TAG, "user: "+user);
    }
}
