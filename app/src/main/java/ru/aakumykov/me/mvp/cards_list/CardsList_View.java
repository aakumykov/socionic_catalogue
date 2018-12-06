package ru.aakumykov.me.mvp.cards_list;

import android.os.Bundle;
import android.support.annotation.Nullable;

import ru.aakumykov.me.mvp.BaseView;
import ru.aakumykov.me.mvp.R;

public class CardsList_View extends BaseView {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cards_list_activity);

        setTitle(R.string.CARDS_LIST_page_title);
        activateUpButton();
    }

    @Override
    public void onUserLogin() {

    }

    @Override
    public void onUserLogout() {

    }
}
