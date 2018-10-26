package ru.aakumykov.me.mvp.test;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.models.Card;
import ru.aakumykov.me.mvp.models.User;

public class Test2Activity extends AppCompatActivity {

    private final static String TAG = "Test-2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test2);

        Intent intent = getIntent();
        Log.d(TAG, "intent: "+intent);

        String action = intent.getAction();

        switch (action) {
            case "CARD":
                prepareCard();
//                finishActivity(10);
                break;

            case "USER":
                prepareUser();
                break;

            default:
                Log.e(TAG, "Неизвестное действие: "+action);
        }
    }

    private void prepareCard() {
        Log.d(TAG, "prepareCard");

        Card card = new Card("1", "TEXT_CARD", "Карточка-1", "Цитата-1", null, "Описание-1");

        Intent resultIntent1 = new Intent();
        setResult(RESULT_OK, resultIntent1);

        resultIntent1.putExtra("CARD", card); // Перенести после setResult() ...

        finish();
    }

    private void prepareUser() {
        Log.d(TAG, "USER");

        User user = new User("Пользователь-1", "Лучший пользователь, да к тому же первый");

        Intent resultIntent2 = new Intent();
        resultIntent2.putExtra("USER", user); // Перенести после setResult() ...

        setResult(RESULT_OK, resultIntent2);

        finish();
    }
}
