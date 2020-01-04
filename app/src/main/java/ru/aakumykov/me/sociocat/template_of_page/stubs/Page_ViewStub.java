package ru.aakumykov.me.sociocat.template_of_page.stubs;

import android.annotation.SuppressLint;

import ru.aakumykov.me.sociocat.BaseView_Stub;
import ru.aakumykov.me.sociocat.template_of_page.iPage;
import ru.aakumykov.me.sociocat.template_of_page.models.Item;

@SuppressLint("Registered")
public class Page_ViewStub extends BaseView_Stub implements iPage.iView {

    @Override
    public void displayItem(Item item) {

    }

    @Override
    public void hideRefreshThrobber() {

    }
}
