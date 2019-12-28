package ru.aakumykov.me.sociocat.page_template.stubs;

import android.annotation.SuppressLint;
import android.widget.Toast;

import ru.aakumykov.me.sociocat.BaseView;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.page_template.iPage;
import ru.aakumykov.me.sociocat.page_template.models.Item;

@SuppressLint("Registered")
public class Page_ViewStub extends BaseView implements iPage.iView {

    @Override
    public void onUserLogin() {

    }

    @Override
    public void onUserLogout() {

    }

    @Override
    public void displayItem(Item item) {

    }

    @Override
    public void hideRefreshThrobber() {

    }
}
