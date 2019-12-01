package ru.aakumykov.me.sociocat.card_show2;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.BaseView;
import ru.aakumykov.me.sociocat.models.Comment;

public class TestActivity extends BaseView {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Comment comment = intent.getParcelableExtra("COMMENT");

        Intent resultIntent = new Intent();
        intent.putExtra("RETURNED_COMMENT", comment);

        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public void onUserLogin() {

    }

    @Override
    public void onUserLogout() {

    }
}
